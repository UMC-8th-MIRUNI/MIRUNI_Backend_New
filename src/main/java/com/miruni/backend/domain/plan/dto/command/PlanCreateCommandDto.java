package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.entity.TimePeriod;

import java.time.LocalDate;

public record PlanCreateCommandDto(
        Long userId,
        String title,
        LocalDate deadline,
        String taskRange,
        Priority priority
) {
    public static PlanCreateCommandDto from(Long userId, AiPlanCreateRequest request) {
        return new PlanCreateCommandDto(
                userId,
                request.title(),
                request.deadline(),
                request.taskRange(),
                request.priority()
        );
    }
}
