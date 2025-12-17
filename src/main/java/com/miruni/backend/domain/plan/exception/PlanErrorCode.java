package com.miruni.backend.domain.plan.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PlanErrorCode implements ErrorCode {
    PLAN_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "PLAN400_1", "일정 타입은 BASIC 또는 AI이어야 합니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
