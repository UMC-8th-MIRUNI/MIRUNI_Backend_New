package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public record BasicPlanCreateCommandDto(
        Long userId,
        String title,
        String description,
        LocalDate scheduledDate,
        LocalTime startTime,
        LocalTime endTime,
        String priority
) {

    public static BasicPlanCreateCommandDto of(Long userId, BasicPlanSaveRequest request) {
        return new BasicPlanCreateCommandDto(
                userId,
                request.title(),
                request.description(),
                request.scheduledDate(),
                request.startTime(),
                request.endTime(),
                request.priority()
        );
    }
}