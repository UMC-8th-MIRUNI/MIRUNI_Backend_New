package com.miruni.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "소셜 로그인 완료(회원가입 완료) 요청 DTO")
public record SocialSignupCompleteRequest(

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
}


