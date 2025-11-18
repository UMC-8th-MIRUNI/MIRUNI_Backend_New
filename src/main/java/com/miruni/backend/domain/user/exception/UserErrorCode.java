package com.miruni.backend.domain.user.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "해당 사용자가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}