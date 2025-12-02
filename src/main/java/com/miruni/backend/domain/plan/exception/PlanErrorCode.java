package com.miruni.backend.domain.plan.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlanErrorCode implements ErrorCode {
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN404_1", "해당 상위 일정이 없습니다.");
    private final HttpStatus status;
    private final String errorCode;
    private final String message;

}
