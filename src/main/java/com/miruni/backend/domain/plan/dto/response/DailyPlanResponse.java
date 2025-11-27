package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.type.PlanType;

import java.time.LocalTime;
import java.util.List;

public record DailyPlanResponse(
        List<DailyPlanItemResponse> unfinishedPlan,
        List<DailyPlanItemResponse> finishedPlan
) {
    public static DailyPlanResponse of(List<DailyPlanItemResponse> unfinishedPlan, List<DailyPlanItemResponse> finishedPlan
    ) {
        return new DailyPlanResponse(
                unfinishedPlan,
                finishedPlan
        );
    }

    public record DailyPlanItemResponse(
            PlanType planType,
            Long planId,
            String title,
            String subTitle,
            LocalTime scheduledTime,
            Priority priority,
            boolean isDone
    ) {
        public static DailyPlanItemResponse fromBasic(BasicPlan plan) {
            return new DailyPlanItemResponse(
                    PlanType.BASIC,
                    plan.getId(),
                    plan.getTitle(),
                    null,
                    plan.getScheduledTime(),
                    plan.getPriority(),
                    plan.isDone()
            );
        }

        public static DailyPlanItemResponse fromAi(AiPlan aiPlan) {
            return new DailyPlanItemResponse(
                    PlanType.AI,
                    aiPlan.getId(),
                    aiPlan.getPlan().getTitle(), // TODO
                    aiPlan.getSubTitle(),
                    aiPlan.getScheduledTime(),
                    aiPlan.getPlan().getPriority(),
                    aiPlan.isDone()
            );
        }
    }
}
