package com.miruni.backend.domain.plan.dto.request;

import com.miruni.backend.domain.plan.type.PlanType;

public record PlanStartRequest(
        Long userId,
        PlanType planType,
        Long planId,
        String durationStr
) {
    public static PlanStartRequest of(Long userId, PlanType planType, Long planId, String durationStr) {
        return new PlanStartRequest(userId, planType, planId, durationStr);
    }
}
