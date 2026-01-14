package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.entity.TimePeriod;
import java.time.LocalDate;

public record AiPlanCreateCommandDto(
        Long planId,
        String title,
        LocalDate deadline,
        TimePeriod timePeriod,
        String taskRange,
        Priority priority,
        String detailRequest
        ) {
    public static AiPlanCreateCommandDto from(AiPlanCreateRequest request, Long planId) {
        return new AiPlanCreateCommandDto(
                planId,
                request.title(),
                request.deadline(),
                request.timePeriod(),
                request.taskRange(),
                request.priority(),
                request.detailRequest()
        );
    }
}
