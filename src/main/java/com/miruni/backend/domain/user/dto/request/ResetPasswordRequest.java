package com.miruni.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "비밀번호 재설정 토큰은 필수입니다.")
        @Schema(description = "이메일 인증 코드 검증으로 발급받은 비밀번호 재설정 토큰", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...")
        String resetToken,

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
        @Schema(description = "새 비밀번호", example = "newPassword123!")
        String newPassword
) {
}


