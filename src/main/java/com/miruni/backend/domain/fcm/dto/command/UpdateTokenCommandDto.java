package com.miruni.backend.domain.fcm.dto.command;

import com.miruni.backend.domain.fcm.dto.request.UpdateTokenRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTokenCommandDto(

        @NotNull
        Long userId,

        @NotBlank
        String deviceId,

        @NotNull
        boolean before5minAlarm,

        @NotNull
        boolean before10minAlarm,

        @NotNull
        boolean popupAlarm,

        @NotNull
        boolean nagAlarm
) {
    public static UpdateTokenCommandDto of(
            UpdateTokenRequestDto request,
            Long userId,
            String deviceId){
        return new UpdateTokenCommandDto(
                userId,
                deviceId,
                request.before5minAlarm(),
                request.before10minAlarm(),
                request.popupAlarm(),
                request.nagAlarm()
                );
    }
}
