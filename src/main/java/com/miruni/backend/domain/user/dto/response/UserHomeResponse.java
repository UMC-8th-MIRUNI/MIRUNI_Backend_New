package com.miruni.backend.domain.user.dto.response;

import com.miruni.backend.domain.user.entity.User;

public record UserHomeResponse(
        String nickname,
        int peanutCount
) {
    public static UserHomeResponse from(User user) {
        return new UserHomeResponse(
                user.getNickname(),
                user.getPeanutCount()
        );
    }
}
