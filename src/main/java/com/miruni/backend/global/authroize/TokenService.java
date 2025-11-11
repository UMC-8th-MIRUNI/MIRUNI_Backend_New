package com.miruni.backend.global.authroize;

import com.miruni.backend.domain.user.dto.TokenDto;
import com.miruni.backend.domain.user.dto.response.UserResponse;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.common.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * 공통 토큰 발급/저장 메서드
     */
    private TokenDto createAndStoreTokens(User user) {
        Authentication authentication = createAuthentication(user);
        TokenDto tokenDto = jwtUtil.createTokenDto(authentication);

        saveRefreshToken(user.getId().toString(), tokenDto.refreshToken(), tokenDto.refreshTokenExp());

        return tokenDto;
    }

    /**
     * 회원가입/로그인 시 토큰 응답 생성
     */
    public UserResponse issueTokenResponse(User user) {
        TokenDto token = createAndStoreTokens(user);
        return UserResponse.of(
                token.accessToken(),
                token.refreshToken(),
                token.accessTokenExp(),
                token.refreshTokenExp()
        );
    }

    /**
     * 인증 객체 생성
     */
    private Authentication createAuthentication(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    /**
     * 리프레시 토큰을 Redis에 저장
     */
    private void saveRefreshToken(String userId, String refreshToken, long expirationTimeInSeconds) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(expirationTimeInSeconds));
        log.info("리프레시 토큰 저장됨: userId={}", userId);
    }

    /**
     * 리프레시 토큰 조회
     */
    public String getRefreshToken(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 리프레시 토큰 삭제
     */
    public void deleteRefreshToken(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("리프레시 토큰 삭제됨: userId={}", userId);
    }

    /**
     * 액세스 토큰을 블랙리스트에 추가 (로그아웃 시)
     */
    public void addToBlacklist(String token, long expirationTimeInSeconds) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofSeconds(expirationTimeInSeconds));
        log.info("토큰이 블랙리스트에 추가됨");
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 로그아웃 처리
     * - 현재 액세스 토큰을 블랙리스트에 추가
     * - Redis에서 리프레시 토큰 삭제
     */
    public void logout(String accessToken, Long userId) {
        // 액세스 토큰 블랙리스트 등록
        long remainingTime = jwtUtil.getRemainingTimeInSeconds(accessToken);
        addToBlacklist(accessToken, remainingTime);
        
        // 리프레시 토큰 삭제
        deleteRefreshToken(userId.toString());
        
        log.info("로그아웃 완료: userId={}", userId);
    }
}

