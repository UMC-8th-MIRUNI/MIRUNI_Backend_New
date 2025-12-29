package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.command.ProfileUpdateCommandDto;
import com.miruni.backend.domain.user.dto.command.UserInfoUpdateCommandDto;
import com.miruni.backend.domain.user.dto.request.ProfileUpdateRequestDto;
import com.miruni.backend.domain.user.dto.request.UserInfoUpdateRequestDto;
import com.miruni.backend.domain.user.dto.response.UserInfoResponseDto;
import com.miruni.backend.domain.user.service.UserQueryService;
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
    private final UserQueryService userQueryService;

    // 회원 탈퇴 API
    @DeleteMapping("/me")
    public void withdrawUser(@AuthToken String accessToken, @LoginUser Long userId) {
        userCommandService.withdrawUser(accessToken, userId);
    }

    @GetMapping("/mypage")
    public UserInfoResponseDto getUserInfo(@LoginUser Long userId) {
        return userQueryService.getUserInfo(userId);
    }

    @PatchMapping("/profile")
    public UserInfoResponseDto updateProfile(@LoginUser Long userId, @RequestBody ProfileUpdateRequestDto requestDto) {
        return userCommandService.updateProfile(ProfileUpdateCommandDto.of(userId, requestDto));
    }

    @PatchMapping("/account")
    public UserInfoResponseDto updateMyInfo(@LoginUser Long userId, @RequestBody UserInfoUpdateRequestDto requestDto
    ) {
        return userCommandService.updateUserInfo(UserInfoUpdateCommandDto.of(userId, requestDto));
    }

}
