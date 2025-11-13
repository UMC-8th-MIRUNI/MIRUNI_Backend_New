package com.miruni.backend.domain.plan.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiPlanErrorCode implements ErrorCode {
    AI_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY,"AI_PLAN_001", "AI 로부터 유효한 응답을 받지 못했습니다."),
    AI_RESPONSE_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_PLAN_002", "AI 응답을 파싱하는 데 실패했습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
