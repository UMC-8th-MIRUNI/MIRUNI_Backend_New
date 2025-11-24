package com.miruni.backend.domain.user.dto.response;

import com.miruni.backend.domain.user.entity.ProfileImage;
import com.miruni.backend.domain.user.entity.User;

import java.time.LocalDate;

public record UserInfoResponseDto(
        Long id,
        String nickname,
        ProfileImage profileImage,
        String name,
        String email,
        String phoneNumber,
        LocalDate birth
) {
    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(
                user.getId(),
                user.getNickname(),
                user.getProfileImage(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getBirth()
        );
    }
}
