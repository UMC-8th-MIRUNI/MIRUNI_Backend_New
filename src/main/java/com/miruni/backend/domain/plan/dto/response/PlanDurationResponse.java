package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.type.PlanType;

public record PlanDurationResponse (

    PlanType planType,
    Long id,
    Long expectedDuration
){
    public static PlanDurationResponse of(PlanType planType, Long id, Long expectedDuration) {
        return new PlanDurationResponse(planType, id, expectedDuration);
    }
}
