package com.miruni.backend.domain.plan.dto.command;


import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BasicPlanUpdateCommand(

        Long userId,
        Long planId,
        String title,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String priority
) {

    public static BasicPlanUpdateCommand of(
            Long userId,
            Long planId,
            BasicPlanSaveRequest request
    ) {
        LocalDate startDate = request.startDate();
        LocalDate endDate = request.endDate();
        LocalTime startTime = request.startTime();
        LocalTime endTime = request.endTime();

        // 시작 시간
        LocalDateTime startDateTime =
                LocalDateTime.of(startDate, startTime);

        LocalDateTime endDateTime;

        if (endDate == null) {
            // 단일 일정
            endDateTime = LocalDateTime.of(startDate, endTime);
        } else {
            // 기간 일정
            endDateTime = LocalDateTime.of(endDate, endTime);
        }

        return new BasicPlanUpdateCommand(
                userId,
                planId,
                request.title(),
                request.description(),
                startDateTime,
                endDateTime,
                request.priority()
        );
    }
}