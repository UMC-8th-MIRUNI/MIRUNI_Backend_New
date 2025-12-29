package com.miruni.backend.domain.plan.dto.command;


import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public record BasicPlanUpdateCommand(

        Long userId,
        Long planId,
        String title,
        String description,
        LocalDate scheduledDate,
        LocalTime startTime,
        LocalTime endTime,
        String priority
) {

    public static BasicPlanUpdateCommand of(Long userId, Long planId, BasicPlanSaveRequest request) {
        return new BasicPlanUpdateCommand(
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