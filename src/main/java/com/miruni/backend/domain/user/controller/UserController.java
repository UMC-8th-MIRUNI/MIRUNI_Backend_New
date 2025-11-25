package com.miruni.backend.domain.user.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.miruni.backend.domain.user.dto.request.EmailVerificationRequest;
import com.miruni.backend.domain.user.dto.request.EmailVerificationVerifyRequest;
import com.miruni.backend.domain.user.dto.request.UserSignupRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.VerifyResponse;
import com.miruni.backend.domain.user.service.EmailVerificationService;
import com.miruni.backend.domain.user.service.UserCommandService;
import com.miruni.backend.global.authroize.AuthToken;
import com.miruni.backend.global.authroize.LoginUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController implements UserApi {

    private final UserCommandService userCommandService;
    private final EmailVerificationService emailVerificationService;

    // 일반 회원가입 API
    @PostMapping
    public JwtResponseDto signup(@Valid @RequestBody UserSignupRequest request) {
        return userCommandService.signup(request);
    }

    // 회원 탈퇴 API
    @DeleteMapping("/me")
    public void withdrawUser(@AuthToken String accessToken, @LoginUser Long userId) {
        userCommandService.withdrawUser(accessToken, userId);
    }

    // 이메일 인증코드 요청 (회원가입 시 사용) - 인증코드 받기
    @PostMapping("/me/email-verification")
    public void requestEmailVerification(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.sendSignUpVerificationCode(request.email());
    }

    // 이메일 인증코드 검증 - 인증하기
    @PostMapping("/me/email-verification/confirm")
    public void verifyEmailVerification(@Valid @RequestBody EmailVerificationVerifyRequest request) {
        emailVerificationService.verifySignUpVerificationCode(request.email(), request.code());
    }

    // 비밀번호 재설정 요청
    @PostMapping("/me/password/reset")
    public void requestPasswordReset(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.requestPasswordReset(request.email());
    }

    // 비밀번호 재설정 코드 검증 - 인증하기
    @PostMapping("/me/password/reset/verification")
    public VerifyResponse verifyPasswordResetCode(@Valid @RequestBody EmailVerificationVerifyRequest request) {
        return emailVerificationService.verifyPasswordResetCode(request);
    }


    // 비밀번호 변경
    // @PatchMapping("/me/password")
    // public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
    //     userCommandService.changePassword(request);
    // }




    // TODO: 추후 구현 예정
    // 내 정보 조회
    // @GetMapping("/me")

    // TODO: 추후 구현 예정
    // 이메일 중복 확인
    // @GetMapping("/email-duplicate")

}
