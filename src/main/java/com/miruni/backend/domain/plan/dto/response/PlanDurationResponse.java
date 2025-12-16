package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.Status;
import com.miruni.backend.domain.plan.type.PlanType;

public record PlanDurationResponse (

    PlanType planType,
    Long id,
    Long expectedDuration,
    Status status
){
    public static PlanDurationResponse of(PlanType planType, Long id, Long expectedDuration, Status status) {
        return new PlanDurationResponse(planType, id, expectedDuration, status);
    }
}
