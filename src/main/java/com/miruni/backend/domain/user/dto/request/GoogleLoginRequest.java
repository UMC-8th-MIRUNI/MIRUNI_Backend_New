package com.miruni.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "구글 소셜 로그인 요청 DTO")
public record GoogleLoginRequest(

        @NotBlank(message = "Google ID 토큰은 필수입니다.")
        @Schema(description = "Google ID Token", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...")
        String googleIdToken
) {
}


