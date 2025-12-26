package com.miruni.backend.domain.user.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER404_1", "이미 사용 중인 닉네임입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER409_2", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_3", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER400_4", "비밀번호가 올바르지 않습니다."),
    AGREEMENT_REQUIRED(HttpStatus.BAD_REQUEST, "USER400_5", "필수 약관에 동의해야 합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "USER401_6", "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "USER401_9", "유효하지 않은 리프레시 토큰입니다."),
    BLACKLISTED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "USER401_10", "사용이 중지된 리프레시 토큰입니다."),
    REFRESH_TOKEN_USER_MISMATCH(HttpStatus.UNAUTHORIZED, "USER401_11", "리프레시 토큰의 사용자 정보가 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER401_12", "저장된 리프레시 토큰이 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "USER401_13", "리프레시 토큰 정보가 서버와 일치하지 않습니다."),
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "USER400_7", "이미 탈퇴한 사용자입니다."),
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "USER401_8", "유효하지 않은 소셜 로그인 토큰입니다."),
    OAUTH_PROVIDER_MISMATCH(HttpStatus.BAD_REQUEST, "USER400_9", "소셜 로그인 제공자 정보가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

