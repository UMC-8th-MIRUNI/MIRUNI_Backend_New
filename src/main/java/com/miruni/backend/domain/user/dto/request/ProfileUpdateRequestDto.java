package com.miruni.backend.domain.user.dto.request;

import com.miruni.backend.domain.user.entity.ProfileImage;

public record ProfileUpdateRequestDto (

        ProfileImage profileImage,
        String nickname
) {

}
