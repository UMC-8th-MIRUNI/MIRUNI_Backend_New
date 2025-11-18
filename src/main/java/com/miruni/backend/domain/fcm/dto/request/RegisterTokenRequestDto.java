package com.miruni.backend.domain.fcm.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterTokenRequestDto(

        @NotBlank
        String token,

        @NotBlank
        String deviceId
) {
}
