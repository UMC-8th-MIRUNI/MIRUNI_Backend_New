package com.miruni.backend.domain.plan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record AiPlanUpdateRequest(
    @Schema(description = "상위일정 제목", example = "상위일정 수정")
    String title,

    @Schema(description = "하위일정 제목", example = "하위일정 수정")
    String sub_title,

    @Schema(description = "실행날짜", example = "2027-01-01")
    LocalDate scheduled_date,

    @Schema(description = "시작시간", example = "14:20:59")
    LocalTime startTime
) {
}
