package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.GoogleLoginRequest;
import com.miruni.backend.domain.user.dto.request.KakaoLoginRequest;
import com.miruni.backend.domain.user.dto.request.LoginRequest;
import com.miruni.backend.domain.user.dto.request.SocialSignupCompleteRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.SocialLoginResponseDto;
import com.miruni.backend.domain.user.entity.OauthProvider;
import com.miruni.backend.domain.user.service.AuthCommandService;
import com.miruni.backend.global.authroize.AuthToken;
import com.miruni.backend.global.authroize.LoginUser;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController implements AuthApi {

    private final AuthCommandService authCommandService;

    // 일반 로그인 API
    @PostMapping("/token")
    public JwtResponseDto login(@Valid @RequestBody LoginRequest request) {
        return authCommandService.login(request);
    }

    // 일반 로그아웃 API
    @DeleteMapping("/token")
    public void logout(@AuthToken String accessToken, @LoginUser Long userId) {
        authCommandService.logout(accessToken, userId);
    }

    // 구글 소셜 로그인 API
    @PostMapping("/social/google")
    public SocialLoginResponseDto loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        return authCommandService.loginWithGoogle(request);
    }

    // 카카오 소셜 로그인 API
    @PostMapping("/social/kakao")
    public SocialLoginResponseDto loginWithKakao(@Valid @RequestBody KakaoLoginRequest request) {
        return authCommandService.loginWithKakao(request);
    }

    // 소셜 로그인 완료(회원가입 완료) API
    @PatchMapping("/social/{provider}")
    public JwtResponseDto completeSocialSignup(
            @PathVariable("provider") OauthProvider provider,
            @LoginUser Long userId,
            @Valid @RequestBody SocialSignupCompleteRequest request
    ) {
        return authCommandService.completeSocialSignup(provider, userId, request);
    }

}