package com.miruni.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    /**
     * 소셜 로그인은 완료됐지만 약관/닉네임 등 최종 가입이 완료되지 않은 상태를 의미.
     * 인가 관점에서는 ROLE_GUEST로 동작한다.
     * */
    PENDING_SIGNUP("ROLE_GUEST"),
    USER("ROLE_USER");

    private final String roleName;
}


