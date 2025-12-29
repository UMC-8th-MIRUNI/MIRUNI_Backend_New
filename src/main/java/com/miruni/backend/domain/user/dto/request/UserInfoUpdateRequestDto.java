package com.miruni.backend.domain.user.dto.request;

import java.time.LocalDate;

public record UserInfoUpdateRequestDto(
        String name,
        LocalDate birth,
        String phoneNumber,
        String email
) {}
