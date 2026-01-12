package com.miruni.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "액세스/리프레시 토큰 재발급 요청 DTO")
public record ReissueTokenRequest(

        @NotBlank(message = "리프레시 토큰은 필수입니다.")
        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9.tenup...")
        String refreshToken
) {
}


