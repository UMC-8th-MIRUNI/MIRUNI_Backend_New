package com.miruni.backend.domain.plan.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BasicPlanErrorCode implements ErrorCode {

    INVALID_PRIORITY_VALUE(HttpStatus.BAD_REQUEST, "BASICPLAN400_1", "잘못된 우선순위 값입니다. 우선순위 값은 '상', '중', '하' 중 하나여야 합니다."),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "BASICPLAN400_2", "일정 시작 시각은 종료 시각보다 이전이어야 합니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}