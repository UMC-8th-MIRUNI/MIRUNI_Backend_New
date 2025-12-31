package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public record BasicPlanCreateCommand(
        Long userId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,     // nullable
        LocalTime startTime,
        LocalTime endTime,
        String priority
) {

    public static BasicPlanCreateCommand of(
            Long userId,
            BasicPlanSaveRequest request
    ) {
        return new BasicPlanCreateCommand(
                userId,
                request.title(),
                request.description(),
                request.startDate(),
                request.endDate(),   // nullable
                request.startTime(),
                request.endTime(),
                request.priority()
        );
    }
}