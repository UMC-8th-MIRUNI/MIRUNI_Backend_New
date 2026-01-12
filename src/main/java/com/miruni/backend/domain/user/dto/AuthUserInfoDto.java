package com.miruni.backend.domain.user.dto;

/**
 * 소셜 로그인으로부터 얻은 사용자 기본 정보 DTO
 */
public record AuthUserInfoDto(
        String email,
        String name
) {

    public static AuthUserInfoDto of(String email, String name) {
        return new AuthUserInfoDto(email, name);
    }
}


