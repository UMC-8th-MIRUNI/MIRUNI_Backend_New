package com.miruni.backend.domain.plan.dto.command;

public record PlanDurationCommand(
        Long userId,
        String planType,
        Long planId
) {

    public static PlanDurationCommand of(Long userId, String planType, Long planId) {
        return new PlanDurationCommand(userId, planType, planId);
    }
}
