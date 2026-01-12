package com.miruni.backend.global.common;

/**
 * 전역 상수 모음.
 */
public final class Constants {

    private Constants() {
    }

    // --- OAuth 외부 API ---
    public static final String GOOGLE_TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";
    public static final String KAKAO_USERINFO_URL = "https://kapi.kakao.com/v2/user/me";
}

