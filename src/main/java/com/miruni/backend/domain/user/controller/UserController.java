package com.miruni.backend.domain.user.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.miruni.backend.domain.user.service.UserCommandService;
import com.miruni.backend.global.authroize.AuthToken;
import com.miruni.backend.global.authroize.LoginUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController implements UserApi {

    private final UserCommandService userCommandService;

    // 회원 탈퇴 API
    @DeleteMapping("/me")
    public void withdrawUser(@AuthToken String accessToken, @LoginUser Long userId) {
        userCommandService.withdrawUser(accessToken, userId);
    }

    // TODO: 추후 구현 예정
    // 내 정보 조회
    // @GetMapping("/me")

}
