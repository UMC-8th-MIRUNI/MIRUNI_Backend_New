package com.miruni.backend.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailVerificationVerifyRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증 코드는 필수입니다.")
        @Pattern(regexp = "^[A-Z0-9]{6}$", message = "인증 코드는 대문자와 숫자로 이루어진 6자리여야 합니다.")
        String code
) {
}


