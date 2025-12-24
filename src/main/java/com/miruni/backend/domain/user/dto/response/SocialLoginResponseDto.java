package com.miruni.backend.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "소셜 로그인 응답 DTO")
public record SocialLoginResponseDto(

        @Schema(description = "회원가입 추가 정보 입력 필요 여부")
        boolean signupRequired,

        @Schema(description = "이번 요청이 신규 소셜 유저인지 여부")
        boolean isNewUser,

        @Schema(description = "발급된 JWT 토큰 정보 (ROLE_GUEST 또는 ROLE_USER)")
        JwtResponseDto tokens
) {

    public static SocialLoginResponseDto signupNeeded(JwtResponseDto tokens, boolean isNewUser) {
        return SocialLoginResponseDto.builder()
                .signupRequired(true)
                .isNewUser(isNewUser)
                .tokens(tokens)
                .build();
    }

    public static SocialLoginResponseDto loggedIn(JwtResponseDto tokens, boolean isNewUser) {
        return SocialLoginResponseDto.builder()
                .signupRequired(false)
                .isNewUser(isNewUser)
                .tokens(tokens)
                .build();
    }
}


