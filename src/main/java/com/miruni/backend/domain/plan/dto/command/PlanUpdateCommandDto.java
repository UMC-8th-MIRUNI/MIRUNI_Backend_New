package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.PlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanTableDto;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.entity.Priority;

import java.time.LocalDate;
import java.util.List;

public record PlanUpdateCommandDto(
        Long userId,
        Long planId,
        String title,
        LocalDate deadline,
        String taskRange,
        Priority priority,
        List<AiPlanTableDto> aiPlans
) {
    public static PlanUpdateCommandDto from(Long userId, Long planId, PlanUpdateRequest request) {
        return new PlanUpdateCommandDto(
                userId,
                planId,
                request.title(),
                request.deadline(),
                request.taskRange(),
                request.priority(),
                request.aiPlans()
        );
    }
}
