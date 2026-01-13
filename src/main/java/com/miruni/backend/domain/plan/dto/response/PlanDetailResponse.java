package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.type.PlanType;

import static com.miruni.backend.global.common.DateTimeFormatUtil.formatTime;

public record PlanDetailResponse(
        PlanType planType,
        Long planId,
        String title,
        String subTitle, // AI
        String description, // BASIC
        String startTime,
        String endTime,
        Priority priority
) {
    public static PlanDetailResponse fromBasic(BasicPlan basicPlan) {
        return new PlanDetailResponse(
                PlanType.BASIC,
                basicPlan.getId(),
                basicPlan.getTitle(),
                null,
                basicPlan.getDescription(),
                formatTime(basicPlan.getStartDateTime()),
                formatTime(basicPlan.getEndDateTime()),
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
                formatTime(aiPlan.getScheduledTime()),
                formatTime(aiPlan.getEndTime()),
                aiPlan.getPlan().getPriority()
        );
    }
}
