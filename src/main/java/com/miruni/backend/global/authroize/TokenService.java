package com.miruni.backend.global.authroize;

import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.global.common.JwtUtil;
import com.miruni.backend.global.common.TokenDto;
import com.miruni.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
        Long userId = user.getId();
        TokenDto tokenDto = jwtUtil.createTokenDto(userId);

        saveRefreshToken(userId.toString(), tokenDto.refreshToken(), tokenDto.refreshTokenExp());

        return tokenDto;
    }

    /**
     * 회원가입/로그인 시 토큰 응답 생성
     */
    public JwtResponseDto issueTokenResponse(User user) {
        TokenDto token = createAndStoreTokens(user);
        return JwtResponseDto.of(
                token.accessToken(),
                token.refreshToken(),
                token.accessTokenExp(),
                token.refreshTokenExp()
        );
    }

    /**
     * 토큰 재발급 (Refresh Token Rotation)
     */
    public JwtResponseDto reissueToken(User user, String refreshToken) {
        Long userId = user.getId();

        try {
            // 토큰 유효성 및 타입 검증
            if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                throw BaseException.type(UserErrorCode.INVALID_REFRESH_TOKEN);
            }

            // 블랙리스트 여부 확인
            if (isTokenBlacklisted(refreshToken)) {
                throw BaseException.type(UserErrorCode.BLACKLISTED_REFRESH_TOKEN);
            }

            // 토큰에 담긴 사용자 정보와 현재 인증된 사용자 일치 여부 확인
            Long tokenUserId = jwtUtil.getUserIdFromToken(refreshToken);
            if (!userId.equals(tokenUserId)) {
                throw BaseException.type(UserErrorCode.REFRESH_TOKEN_USER_MISMATCH);
            }

            // Redis 에 저장된 리프레시 토큰 조회
            String storedRefreshToken = getRefreshToken(userId.toString());
            if (storedRefreshToken == null) {
                throw BaseException.type(UserErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }
            if (!refreshToken.equals(storedRefreshToken)) {
                throw BaseException.type(UserErrorCode.REFRESH_TOKEN_MISMATCH);
            }

            // 기존 리프레시 토큰 삭제 (Rotation)
            deleteRefreshToken(userId.toString());

            // 새 토큰 발급 및 저장
            TokenDto token = createAndStoreTokens(user);

            log.info("토큰 재발급 완료: userId={}", userId);

            return JwtResponseDto.of(
                    token.accessToken(),
                    token.refreshToken(),
                    token.accessTokenExp(),
                    token.refreshTokenExp()
            );
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("토큰 재발급 중 오류 발생: {}", e.getMessage());
            throw BaseException.type(UserErrorCode.INVALID_REFRESH_TOKEN);
        }
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

