package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AiPlanCreateResponse(
        @NotBlank
        @Schema(description = "상위 일정 Id", example = "1")
        Long planId,

        @NotBlank
        @Schema(description = "분할 일정 Id", example = "1")
        Long ai_planId,

        @NotBlank
        @Schema(description = "상위 일정 제목", example = "UMC 기획안 만들기")
        String title,

        @NotNull
        @Schema(description = "마감기한", example = "2025-12-31")
        LocalDate deadline,

        @NotBlank
        @Schema(description = "일정 범위", example = "기획안 13페이지 작성")
        String taskRange,

        @NotNull
        @Schema(description = "우선 순위", example = "HIGH")
        Priority priority,

        @NotNull
        @Schema(description = "수행날짜", example = "2025-12-05")
        LocalDate scheduled_date,

        @NotBlank
        @Schema(description = "분할 일정 제목", example = "PPT 주제 선정")
        String description,

        @NotBlank
        @Schema(description = "예상 소요 시간", example = "90")
        Long expected_duration,

        @NotNull
        @Schema(description = "시작 시간", example = "09:00:00")
        LocalTime startTime,

        @NotNull
        @Schema(description = "종료 시간", example = "10:30:00")
        LocalTime endTime
) {
        public AiPlan toEntity(Plan plan) {
                return AiPlan.builder()
                        .plan(plan)
                        .subTitle(this.description())
                        .scheduledDate(this.scheduled_date())
                        .scheduledTime(this.startTime())
                        .expectedDuration(this.expected_duration.intValue())
                        .build();
        }

        public static AiPlanCreateResponse fromEntity(AiPlan aiPlan, Plan plan) {
                return new AiPlanCreateResponse(
                        plan.getId(),
                        aiPlan.getId(),
                        plan.getTitle(),
                        plan.getDeadline().toLocalDate(),
                        plan.getScope(),
                        plan.getPriority(),
                        aiPlan.getScheduledDate(),
                        aiPlan.getSubTitle(),
                        (long) aiPlan.getExpectedDuration(),
                        aiPlan.getScheduledTime(),
                        aiPlan.getScheduledTime().plusMinutes(aiPlan.getExpectedDuration())
                );
        }
}
