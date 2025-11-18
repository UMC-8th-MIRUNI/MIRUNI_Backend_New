package com.miruni.backend.domain.fcm.dto.response;

public record RegisterTokenResponseDto(
        Long tokenId
) {
    public static RegisterTokenResponseDto from(Long tokenId) {
        return new RegisterTokenResponseDto(tokenId);
    }
}
