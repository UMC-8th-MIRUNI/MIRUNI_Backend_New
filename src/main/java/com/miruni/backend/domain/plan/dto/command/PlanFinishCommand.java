package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.type.PlanType;

public record PlanFinishCommand(
        PlanType planType,
        Long planId,
        Long userId,
        String expectedTime, // "HH:mm" 형식
        String actualTime    // "HH:mm" 형식
) {
    public static PlanFinishCommand of(PlanType planType, Long planId, Long userId, String expectedTime, String actualTime) {
        return new PlanFinishCommand(planType, planId, userId, expectedTime, actualTime);
    }
}