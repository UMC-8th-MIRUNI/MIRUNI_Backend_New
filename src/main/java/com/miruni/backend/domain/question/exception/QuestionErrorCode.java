package com.miruni.backend.domain.question.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum QuestionErrorCode implements ErrorCode {

    AGREEMENT_NOT_ACCEPT(HttpStatus.BAD_REQUEST, "QUESTION_001", "프라이버시 동의가 이루어지지 않았습니다."),
    FAILED_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR,"QUESTION_002", "")
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
