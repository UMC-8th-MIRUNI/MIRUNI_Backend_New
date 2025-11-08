package com.miruni.backend.domain.plan.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BasicPlanErrorCode implements ErrorCode {

    INVALID_PRIORITY_VALUE(HttpStatus.BAD_REQUEST, "BASICPLAN400_1", "잘못된 우선순위 값입니다. 우선순위 값은 '상', '중', '하' 중 하나여야 합니다."),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "BASICPLAN400_2", "일정 시작 시각은 종료 시각보다 이전이어야 합니다."),

    BASIC_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN404_1", "해당 일반 일정이 존재하지 않습니다."),
    USER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "PLAN403_1", "해당 일정에 대한 권한이 없습니다.");
    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}