package com.miruni.backend.domain.plan.dto.request;

import com.miruni.backend.domain.plan.dto.response.AiPlanTableDto;
import com.miruni.backend.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record PlanUpdateRequest(
    @Schema(description = "상위일정 id", example = "1")
    Long planId,

    @Schema(description = "상위일정 제목", example = "UMC 기획안 만들기")
    String title,

    @Schema(description = "마감기한", example = "2025-05-30")
    LocalDate deadline,

    @Schema(description = "일정범위", example = "기획안 13페이지 완성")
    String taskRange,

    @Schema(description = "우선순위", example = "HIGH")
    Priority priority,

    @Schema(description = "세부 일정 스케줄표")
    List<AiPlanTableDto> aiPlans
) {
    public static PlanUpdateRequest of(Long planId, String title, LocalDate deadline, String taskRange, Priority priority, List<AiPlanTableDto> aiPlans) {
        return new PlanUpdateRequest(planId, title, deadline, taskRange, priority, aiPlans);
    }
}