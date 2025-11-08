package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;

import java.time.LocalDate;
import java.time.LocalTime;

public record BasicPlanResponse(
        Long id,
        Long userId,
        String title,
        String description,
        LocalDate scheduledDate,
        LocalTime scheduledTime,
        Long expectedDuration,
        boolean isDone,
        Priority priority
) {
    public static BasicPlanResponse from(BasicPlan plan) {
        return new BasicPlanResponse(
                plan.getId(),
                plan.getUser().getId(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getScheduledDate(),
                plan.getScheduledTime(),
                plan.getExpectedDuration(),
                plan.isDone(),
                plan.getPriority()
        );
    }
}