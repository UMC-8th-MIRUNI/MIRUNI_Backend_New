package com.miruni.backend.domain.user.dto.response;

import lombok.Builder;

@Builder
public record JwtResponseDto(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long accessTokenExpiresIn,
    Long refreshTokenExpiresIn
) {
    public static JwtResponseDto of(String accessToken, String refreshToken, long accessTokenExp, long refreshTokenExp) {
        return JwtResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExp)
                .refreshTokenExpiresIn(refreshTokenExp)
                .build();
    }
} 