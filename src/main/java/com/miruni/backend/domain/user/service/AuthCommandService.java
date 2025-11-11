package com.miruni.backend.domain.user.service;

import com.miruni.backend.domain.user.dto.request.LoginRequest;
import com.miruni.backend.domain.user.dto.response.UserResponse;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.authroize.TokenService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    
    /**
     * 일반 로그인
     */
    public UserResponse login(LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw BaseException.type(UserErrorCode.INVALID_PASSWORD);
        }
        
        // 토큰 발급
        log.info("로그인 성공: email={}, userId={}", request.email(), user.getId());
        return tokenService.issueTokenResponse(user);
    }

    /**
     *  일반 로그아웃
     */
    public void logout(String accessToken, Long userId) {
        tokenService.logout(accessToken, userId);
    }
}

