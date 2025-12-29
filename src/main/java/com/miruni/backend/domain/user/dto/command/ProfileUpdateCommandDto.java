package com.miruni.backend.domain.user.dto.command;

import com.miruni.backend.domain.user.dto.request.ProfileUpdateRequestDto;
import com.miruni.backend.domain.user.entity.ProfileImage;

public record ProfileUpdateCommandDto(
        Long userId,
        ProfileImage profileImage,
        String nickname
) {
    public static ProfileUpdateCommandDto of(Long userId, ProfileUpdateRequestDto request) {
        return new ProfileUpdateCommandDto(
                userId,
                request.profileImage(),
                request.nickname()
        );
    }
}
