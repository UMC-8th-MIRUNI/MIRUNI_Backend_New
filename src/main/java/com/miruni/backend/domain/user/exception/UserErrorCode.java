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
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "USER400_7", "이미 탈퇴한 사용자입니다.");
    
    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

