package com.miruni.backend.domain.user.dto.command;

import com.miruni.backend.domain.user.dto.request.UserInfoUpdateRequestDto;

import java.time.LocalDate;

public record UserInfoUpdateCommandDto(
        Long userId,
        String name,
        LocalDate birth,
        String phoneNumber,
        String email
) {
    public static UserInfoUpdateCommandDto of(Long userId, UserInfoUpdateRequestDto request) {
        return new UserInfoUpdateCommandDto(
                userId,
                request.name(),
                request.birth(),
                request.phoneNumber(),
                request.email()
        );
    }
}
