package com.miruni.backend.domain.fcm.controller;

import com.miruni.backend.domain.fcm.dto.command.RegisterTokenCommandDto;
import com.miruni.backend.domain.fcm.dto.command.UpdateTokenCommandDto;
import com.miruni.backend.domain.fcm.dto.request.RegisterTokenRequestDto;
import com.miruni.backend.domain.fcm.dto.request.UpdateTokenRequestDto;
import com.miruni.backend.domain.fcm.service.FcmTokenCommandService;
import com.miruni.backend.global.authroize.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {

    private final FcmTokenCommandService fcmTokenCommandService;

    @PostMapping("/tokens")
    public void registerToken(
            @Valid @RequestBody RegisterTokenRequestDto request,
            @LoginUser Long userId) {
        fcmTokenCommandService.registerToken(RegisterTokenCommandDto.of(request, userId));
    }

    @PatchMapping("/tokens/{deviceId}")
    public void updateToken(
            @Valid @RequestBody UpdateTokenRequestDto request,
            @PathVariable String deviceId,
            @LoginUser Long userId
    ){
        fcmTokenCommandService.updateToken(UpdateTokenCommandDto.of(request, userId, deviceId));
    }
}
