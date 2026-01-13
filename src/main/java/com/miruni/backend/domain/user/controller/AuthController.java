package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.GoogleLoginRequest;
import com.miruni.backend.domain.user.dto.request.KakaoLoginRequest;
import com.miruni.backend.domain.user.dto.request.LoginRequest;
import com.miruni.backend.domain.user.dto.request.ReissueTokenRequest;
import com.miruni.backend.domain.user.dto.request.SocialSignupCompleteRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.SocialLoginResponseDto;
import com.miruni.backend.domain.user.entity.OauthProvider;
import com.miruni.backend.domain.user.dto.request.EmailVerificationRequest;
import com.miruni.backend.domain.user.dto.request.EmailVerificationVerifyRequest;
import com.miruni.backend.domain.user.dto.request.LoginRequest;
import com.miruni.backend.domain.user.dto.request.ResetPasswordRequest;
import com.miruni.backend.domain.user.dto.request.UserSignupRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.VerifyResponse;
import com.miruni.backend.domain.user.service.AuthCommandService;
import com.miruni.backend.domain.user.service.UserCommandService;
import com.miruni.backend.domain.user.service.VerificationService;
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
    private final UserCommandService userCommandService;
    private final VerificationService verificationService;

    // 일반 로그인 API
    @PostMapping("/token")
    public JwtResponseDto login(@Valid @RequestBody LoginRequest request) {
        return authCommandService.login(request);
    }

    // 액세스/리프레시 토큰 재발급 API
    @PostMapping("/token/refresh")
    public JwtResponseDto refreshToken(
            @LoginUser Long userId,
            @Valid @RequestBody ReissueTokenRequest request
    ) {
        return authCommandService.reissueToken(userId, request.refreshToken());
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
  
    // 일반 회원가입 API
    @PostMapping("/signup")
    public JwtResponseDto signup(@Valid @RequestBody UserSignupRequest request) {
        return userCommandService.signup(request);
    }

    // 회원가입 이메일 인증코드 요청
    @PostMapping("/signup/email-verification")
    public void requestEmailVerification(@Valid @RequestBody EmailVerificationRequest request) {
        verificationService.sendSignUpVerificationCode(request.email());
    }

    // 회원가입 이메일 인증코드 검증
    @PostMapping("/signup/email-verification/confirm")
    public void verifyEmailVerification(@Valid @RequestBody EmailVerificationVerifyRequest request) {
        verificationService.verifySignUpVerificationCode(request.email(), request.code());
    }

    // 비밀번호 재설정 요청
    @PostMapping("/password/reset")
    public void requestPasswordReset(@Valid @RequestBody EmailVerificationRequest request) {
        verificationService.requestPasswordReset(request.email());
    }

    // 비밀번호 재설정 코드 검증 - resetToken 발급
    @PostMapping("/password/reset/verification")
    public VerifyResponse verifyPasswordResetCode(@Valid @RequestBody EmailVerificationVerifyRequest request) {
        return verificationService.verifyPasswordResetCode(request);
    }

    // 비밀번호 재설정 완료 - resetToken으로 새 비밀번호 설정
    @PostMapping("/password/reset/confirm")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userCommandService.resetPassword(request);
    }

}