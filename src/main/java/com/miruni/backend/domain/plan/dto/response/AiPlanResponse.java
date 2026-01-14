package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record AiPlanResponse(
        @NotNull
        @Schema(description = "상위 일정 ID", example = "1")
        Long planId,

        @NotBlank
        @Schema(description = "상위 일정 제목", example = "UMC 기획안 만들기")
        String title,

        @NotNull
        @Schema(description = "마감기한", example = "2025-05-30")
        LocalDate deadline,

        @NotBlank
        @Schema(description = "일정 범위", example = "기획안 13페이지 작성")
        String taskRange,

        @NotNull
        @Schema(description = "우선 순위", example = "HIGH")
        Priority priority,

        @Schema(description = "진행률", example = "30")
        int progressRate,

        @NotNull
        @Schema(description = "세부 일정 스케줄표")
        List<AiPlanTableDto> aiPlans
) {
    public static AiPlanResponse of(Long planId, String title, LocalDate deadline, String taskRange, Priority priority, int progressRate, List<AiPlanTableDto> aiPlans) {
        return new AiPlanResponse(planId, title, deadline, taskRange, priority, progressRate, aiPlans);
    }
}
