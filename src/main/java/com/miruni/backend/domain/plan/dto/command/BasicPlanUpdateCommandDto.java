package com.miruni.backend.domain.plan.dto.command;


import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public record BasicPlanUpdateCommandDto(

        Long userId,
        Long planId,
        String title,
        String description,
        LocalDate scheduledDate,
        LocalTime startTime,
        LocalTime endTime,
        String priority
) {

    public static BasicPlanUpdateCommandDto of(Long userId, Long planId, BasicPlanSaveRequest request) {
        return new BasicPlanUpdateCommandDto(
                userId,
                planId,
                request.title(),
                request.description(),
                request.scheduledDate(),
                request.startTime(),
                request.endTime(),
                request.priority()
        );
    }
}