package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.type.PlanType;

public record PlanDurationCommand (
        Long userId,
        PlanType planType,
        Long planId
) {
    public static PlanDurationCommand of(Long userId, PlanType planType, Long planId) {
        return new PlanDurationCommand(userId, planType, planId);
    }
}
