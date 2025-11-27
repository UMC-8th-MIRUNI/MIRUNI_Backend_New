package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;

import java.time.LocalDate;
import java.time.LocalTime;

public record PlanDetailResponse(
        String planType,
        Long planId,
        String title,
        String subTitle, // AI
        String description, // BASIC
        LocalDate scheduledDate,
        LocalTime scheduledTime,
        Priority priority
) {
    public static PlanDetailResponse fromBasic(BasicPlan basicPlan) {
        return new PlanDetailResponse(
                "BASIC",
                basicPlan.getId(),
                basicPlan.getTitle(),
                null,
                basicPlan.getDescription(),
                basicPlan.getScheduledDate(),
                basicPlan.getScheduledTime(),
                basicPlan.getPriority()
        );
    }

    public static PlanDetailResponse fromAi(AiPlan aiPlan) {
        return new PlanDetailResponse(
                "AI",
                aiPlan.getId(),
                aiPlan.getPlan().getTitle(),
                aiPlan.getSubTitle(),
                null,
                aiPlan.getScheduledDate(),
                aiPlan.getScheduledTime(),
                aiPlan.getPlan().getPriority()
        );
    }
}
