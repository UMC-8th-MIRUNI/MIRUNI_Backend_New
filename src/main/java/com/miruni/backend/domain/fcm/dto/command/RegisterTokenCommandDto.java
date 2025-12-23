package com.miruni.backend.domain.fcm.dto.command;

import com.miruni.backend.domain.fcm.dto.request.RegisterTokenRequestDto;

public record RegisterTokenCommandDto(
        String token,

        String deviceId,

        boolean before5minAlarm,

        boolean before10minAlarm,

        boolean popupAlarm,

        boolean nagAlarm,

        Long userId
) {
    public static RegisterTokenCommandDto of(RegisterTokenRequestDto request,  Long userId) {
        return  new RegisterTokenCommandDto(
                request.token(),
                request.deviceId(),
                request.before5minAlarm(),
                request.before10minAlarm(),
                request.popupAlarm(),
                request.nagAlarm(),
                userId);
    }
}
