package com.miruni.backend.domain.plan.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiPlanErrorCode implements ErrorCode {
    AI_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY,"AI_PLAN502_001", "AI 로부터 유효한 응답을 받지 못했습니다."),
    AI_RESPONSE_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_PLAN500_002", "AI 응답을 파싱하는 데 실패했습니다."),
    AI_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "AI_PLAN404_1", "해당 AI 계획이 존재하지 않습니다."),
    DEADLINE_AFTER(HttpStatus.BAD_REQUEST, "AI_PLAN400_001", "진행날짜는 마감기한 이전이어야 합니다."),
    INVALID_TIME_DURATION(HttpStatus.BAD_REQUEST, "AI_PLAN400_002", "예상 소요시간은 종료 시간과 시작 시간의 차이와 같아야 합니다."),
    PLAN_NOT_MATCH(HttpStatus.BAD_REQUEST, "AI_PLAN400_003", "상위 일정 아이디가 다릅니다."),
    AI_PLAN_CONFLICT(HttpStatus.BAD_REQUEST, "AI_PLAN400_004", "같은 시간에 다른 일정이 예정되어 있습니다."),

    AI_PLAN_FORBIDDEN(HttpStatus.FORBIDDEN, "AI_PLAN403_1", "해당 일정에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
