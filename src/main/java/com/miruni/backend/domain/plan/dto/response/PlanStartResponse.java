package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.Status;
import com.miruni.backend.domain.plan.type.PlanType;

public record PlanStartResponse(
        PlanType planType,
        Long planId,
        Status status
) {
        public static PlanStartResponse of(PlanType planType, Long planId, Status status) {
            return new PlanStartResponse(planType, planId, status);
        }
    }
