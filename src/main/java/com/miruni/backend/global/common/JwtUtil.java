package com.miruni.backend.global.common;

import com.miruni.backend.domain.user.dto.TokenDto;
import com.miruni.backend.global.authroize.CustomUserDetails;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import com.miruni.backend.global.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest; // Spring Boot 3 기준 (부트2면 javax.servlet로 변경)
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    // --- 토큰 타입 상수 & 클레임 키 ---
    private static final String CLAIM_TOKEN_TYPE = "type";
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";
    private static final String TOKEN_TYPE_TEMP = "TEMP";

    // 이메일 인증 등에서 사용할 임시 토큰 만료시간 (고정)
    private static final long TEMP_TOKEN_EXPIRATION = 1000L * 60 * 5; // 5분

    // ===== 공통 내부 로직 =====

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Long extractUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    private String generateToken(Long userId, String tokenType, long validityMs) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiresAt = new Date(now + validityMs);    

        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    // ===== 토큰 생성 =====

    /** Access Token 생성 */
    public String generateAccessToken(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return generateToken(userId, TOKEN_TYPE_ACCESS, jwtProperties.accessTokenExpiration());
    }

    /** Refresh Token 생성 */
    public String generateRefreshToken(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return generateToken(userId, TOKEN_TYPE_REFRESH, jwtProperties.refreshTokenExpiration());
    }

    /** 임시 토큰 생성 (이메일 인증 등에 사용) */
    public String generateTempToken(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return generateToken(userId, TOKEN_TYPE_TEMP, TEMP_TOKEN_EXPIRATION);
    }

    /** Access + Refresh 한 번에 생성해서 DTO로 리턴 */
    public TokenDto createTokenDto(Authentication authentication) {
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);

        long accessTokenExp = getAccessTokenExpirationInSeconds();
        long refreshTokenExp = getRefreshTokenExpirationInSeconds();

        return TokenDto.of(accessToken, refreshToken, accessTokenExp, refreshTokenExp);
    }

    // ===== 만료 관련 유틸 =====

    public long getAccessTokenExpirationInSeconds() {
        return jwtProperties.accessTokenExpiration() / 1000;
    }

    public long getRefreshTokenExpirationInSeconds() {
        return jwtProperties.refreshTokenExpiration() / 1000;
    }

    /** 토큰의 남은 유효 시간 (초 단위) */
    public long getRemainingTimeInSeconds(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            if (expiration == null) {
                return 0;
            }

            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remainingTime / 1000);
        } catch (Exception e) {
            log.warn("토큰 남은 시간 계산 실패: {}", e.getMessage());
            return 0;
        }
    }

    // ===== 검증 / 파싱 =====

    /** 토큰 검증 (서명 + 만료) */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /** 토큰에서 사용자 ID 추출 (실패 시 UNAUTHORIZED 예외) */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
            throw BaseException.type(CommonErrorCode.UNAUTHORIZED);
        }
    }

    /** 토큰 타입(ACCESS/REFRESH/TEMP) 추출 */
    public String getTokenType(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get(CLAIM_TOKEN_TYPE, String.class);
        } catch (Exception e) {
            log.warn("토큰 타입 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    public boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(getTokenType(token));
    }

    public boolean isTempToken(String token) {
        return TOKEN_TYPE_TEMP.equals(getTokenType(token));
    }

    // ===== HTTP 요청에서 토큰 꺼내는 헬퍼 =====

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     */
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}