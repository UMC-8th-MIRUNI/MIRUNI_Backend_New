package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.type.PlanType;

import java.time.LocalDate;

import static com.miruni.backend.global.common.DateTimeFormatUtil.formatTime;


public record PlanDetailResponse(
        PlanType planType,
        Long planId,
        String title,
        String subTitle, // AI
        String description, // BASIC
        LocalDate scheduledDate,
        String scheduledTime,
        Priority priority
) {
    public static PlanDetailResponse fromBasic(BasicPlan basicPlan) {
        return new PlanDetailResponse(
                PlanType.BASIC,
                basicPlan.getId(),
                basicPlan.getTitle(),
                null,
                basicPlan.getDescription(),
                basicPlan.getScheduledDate(),
                formatTime(basicPlan.getScheduledTime()),
                basicPlan.getPriority()
        );
    }

    public static PlanDetailResponse fromAi(AiPlan aiPlan) {
        return new PlanDetailResponse(
                PlanType.AI,
                aiPlan.getId(),
                aiPlan.getPlan().getTitle(),
                aiPlan.getSubTitle(),
                null,
                aiPlan.getScheduledDate(),
                formatTime(aiPlan.getScheduledTime()),
                aiPlan.getPlan().getPriority()
        );
    }
}
