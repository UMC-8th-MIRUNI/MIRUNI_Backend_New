package com.miruni.backend.domain.user.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.miruni.backend.domain.user.dto.request.UserSignupRequest;
import com.miruni.backend.domain.user.dto.response.UserResponse;
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

    // 일반 회원가입 API
    @PostMapping
    public UserResponse signup(@Valid @RequestBody UserSignupRequest request) {
        return userCommandService.signup(request);
    }

    // 회원 탈퇴 API
    @DeleteMapping("/me")
    public void withdrawUser(@AuthToken String accessToken, @LoginUser Long userId) {
        userCommandService.withdrawUser(accessToken, userId);
    }

    // TODO: 추후 구현 예정
    // 이메일 인증 요청
    // @PostMapping("/me/email-verification")

    // TODO: 추후 구현 예정
    // 내 정보 조회
    // @GetMapping("/me")

    // TODO: 추후 구현 예정
    // 이메일 중복 확인
    // @GetMapping("/email-duplicate")

    // TODO: 추후 구현 예정
    // 비밀번호 변경
    // @PatchMapping("/me/password")
}
