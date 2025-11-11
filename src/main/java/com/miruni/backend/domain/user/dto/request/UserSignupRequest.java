package com.miruni.backend.domain.user.dto.request;

import com.miruni.backend.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserSignupRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
        @Schema(description = "이메일 주소", example = "dhzktldh@gmail.com")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
        @Schema(description = "사용자 닉네임", example = "추추")
        String nickname,

        @NotNull(message = "이용약관 동의는 필수입니다.")
        @Schema(description = "이용약관 동의", example = "true")
        Boolean serviceAgreed,

        @NotNull(message = "개인정보 수집 및 이용 동의는 필수입니다.")
        @Schema(description = "개인정보 수집 및 이용 동의", example = "true")
        Boolean privacyAgreed,

        @Schema(description = "마케팅 정보 수신 동의 (선택)", example = "false")
        Boolean marketingAgreed

) {
        public User toEntity(String encodedPassword) {
                return User.builder()
                        .email(email)
                        .password(encodedPassword)
                        .nickname(nickname)
                        .peanutCount(0)
                        .oauthProvider(null)
                        .build();
        }
}