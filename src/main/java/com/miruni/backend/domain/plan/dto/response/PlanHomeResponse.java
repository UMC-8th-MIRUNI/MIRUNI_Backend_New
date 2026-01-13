package com.miruni.backend.domain.plan.dto.response;

import java.util.List;

public record PlanHomeResponse(
        int progressRate,
        List<DailyPlanResponse.DailyPlanItemResponse> todayPlans
) {

    public static PlanHomeResponse of(int progressRate, List<DailyPlanResponse.DailyPlanItemResponse> todayPlans) {
        return new PlanHomeResponse(
                progressRate,
                todayPlans
        );
    }
}
