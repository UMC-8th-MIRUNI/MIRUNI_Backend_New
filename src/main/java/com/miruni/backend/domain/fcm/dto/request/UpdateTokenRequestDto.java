package com.miruni.backend.domain.fcm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTokenRequestDto(

        @NotNull
        boolean before5minAlarm,

        @NotNull
        boolean before10minAlarm,

        @NotNull
        boolean popupAlarm,

        @NotNull
        boolean nagAlarm
) {
}
