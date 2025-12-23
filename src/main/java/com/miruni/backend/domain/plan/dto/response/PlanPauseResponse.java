package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.type.PlanType;

public record PlanPauseResponse(
        PlanType planType,
        Long planId,
        String resumeTime
) {
    public static PlanPauseResponse of(PlanType planType, Long planId, String resumeTime) {
        return new PlanPauseResponse(planType, planId, resumeTime);
    }
}
