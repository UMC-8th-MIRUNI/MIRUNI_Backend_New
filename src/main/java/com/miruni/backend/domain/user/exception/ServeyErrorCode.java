package com.miruni.backend.domain.user.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServeyErrorCode implements ErrorCode {

    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY404_1", "설문조사를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

