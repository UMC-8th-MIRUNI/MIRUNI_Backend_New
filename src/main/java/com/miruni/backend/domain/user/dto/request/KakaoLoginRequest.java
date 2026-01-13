package com.miruni.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "카카오 소셜 로그인 요청 DTO")
public record KakaoLoginRequest(

        @NotBlank(message = "카카오 액세스 토큰은 필수입니다.")
        @Schema(description = "카카오 액세스 토큰", example = "kakao-access-token")
        String accessToken
) {
}


