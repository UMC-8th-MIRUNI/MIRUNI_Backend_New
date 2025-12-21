package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.type.PlanType;

public record PlanPauseCommand(
        PlanType planType,
        Long planId,
        Long userId,
        String resumeTime // "HH:mm" 형식
) {
    public static PlanPauseCommand of(PlanType planType, Long planId, Long userId, String resumeTime) {
        return new PlanPauseCommand(planType, planId, userId, resumeTime);
    }
}
