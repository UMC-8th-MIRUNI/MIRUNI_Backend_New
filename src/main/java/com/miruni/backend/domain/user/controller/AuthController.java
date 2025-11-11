package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.LoginRequest;
import com.miruni.backend.domain.user.dto.response.UserResponse;
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
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return authCommandService.login(request);
    }

    // 일반 로그아웃 API
    @DeleteMapping("/token")
    public void logout(@AuthToken String accessToken, @LoginUser Long userId) {
        authCommandService.logout(accessToken, userId);
    }

}