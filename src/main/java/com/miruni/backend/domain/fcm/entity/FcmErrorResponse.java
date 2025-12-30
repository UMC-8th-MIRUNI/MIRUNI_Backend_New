package com.miruni.backend.domain.fcm.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FcmErrorResponse {
    //토큰 삭제 필요 오류
    UNREGISTERED(true),
    INVALID_ARGUMENT(true),
    NOT_FOUND(true),
    SENDER_ID_MISMATCH(true),
    INVALID_REGISTRATION(true),
    REGISTRATION_TOKEN_NOT_REGISTERED(true),

    // 일시적 요류 (토큰 유지)
    UNAVAILABLE(false),
    INTERNAL(false),
    DEADLINE_EXCEEDED(false),
    QUOTA_EXCEEDED(false),
    UNKNOWN(false)
    ;


    private final boolean shouldDeleteToken;

    public static FcmErrorResponse fromErrorCode(String errorCode){
        if(errorCode == null)
            return UNKNOWN;

        try{
            return FcmErrorResponse.valueOf(errorCode);
        } catch(IllegalArgumentException e){
            return UNKNOWN;
        }
    }

}
