package com.miruni.backend.domain.question.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum QuestionErrorCode implements ErrorCode {

    AGREEMENT_NOT_ACCEPT(HttpStatus.BAD_REQUEST, "QUESTION_001", "프라이버시 동의가 이루어지지 않았습니다."),
    FAILED_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR,"QUESTION_002", "이미지 업로드가 실패했습니다"),
    EXCEED_MAX_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "QUESTION_003", "이미지 개수는 5개를 초과할 수 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_004", "해당하는 id의 이미지를 찾을 수 없습니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_005", "해당하는 id의 질문을 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
