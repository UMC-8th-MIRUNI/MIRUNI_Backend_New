package com.miruni.backend.domain.fcm.controller;

import com.miruni.backend.domain.fcm.dto.command.RegisterTokenCommandDto;
import com.miruni.backend.domain.fcm.dto.request.RegisterTokenRequestDto;
import com.miruni.backend.domain.fcm.dto.response.RegisterTokenResponseDto;
import com.miruni.backend.domain.fcm.service.FcmTokenCommandService;
import com.miruni.backend.global.authroize.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {

    private final FcmTokenCommandService fcmTokenCommandService;

    @PostMapping("/tokens")
    public RegisterTokenResponseDto registerToken(
            @Valid @RequestBody RegisterTokenRequestDto request,
            @LoginUser Long userId) {
        return fcmTokenCommandService.registerToken(RegisterTokenCommandDto.of(request, userId));
    }
}
