package com.miruni.backend.global.common;

import lombok.Builder;

@Builder
public record TokenDto(
        String accessToken,
        String refreshToken,
        long accessTokenExp,
        long refreshTokenExp
) {
    public static TokenDto of(String accessToken, String refreshToken, long accessTokenExp, long refreshTokenExp) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExp(accessTokenExp)
                .refreshTokenExp(refreshTokenExp)
                .build();
    }
}

