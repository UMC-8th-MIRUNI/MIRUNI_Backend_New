package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.entity.TimePeriod;

import java.time.LocalDateTime;

public record AiPlanCreateCommandDto(
        Long planId,
        String title,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        TimePeriod timePeriod,
        String scope,
        Priority priority,
        String detailRequest
        ) {
    public static AiPlanCreateCommandDto from(AiPlanCreateRequest request, Long planId) {
        return new AiPlanCreateCommandDto(
                planId,
                request.title(),
                request.startDateTime(),
                request.endDateTime(),
                request.timePeriod(),
                request.scope(),
                request.priority(),
                request.detailRequest()
        );
    }
}
