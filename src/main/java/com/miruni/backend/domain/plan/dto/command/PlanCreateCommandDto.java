package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.entity.Priority;

import java.time.LocalDateTime;

public record PlanCreateCommandDto(
        Long userId,
        String title,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String taskRange,
        Priority priority
) {
    public static PlanCreateCommandDto from(Long userId, AiPlanCreateRequest request) {
        return new PlanCreateCommandDto(
                userId,
                request.title(),
                request.startDateTime(),
                request.endDateTime(),
                request.scope(),
                request.priority()
        );
    }
}
