package com.miruni.backend.domain.plan.dto.command;


import com.miruni.backend.domain.plan.dto.request.BasicPlanUpdateRequest;

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
            BasicPlanUpdateRequest request
    ) {
        LocalDate date = request.date();
        LocalTime startTime = request.startTime();
        LocalTime endTime = request.endTime();

        // 시작 일시
        LocalDateTime startDateTime =
                LocalDateTime.of(date, startTime);

        // 종료 일시 (자정 넘김 처리)
        LocalDate endDate = endTime.isBefore(startTime)
                ? date.plusDays(1)
                : date;

        LocalDateTime endDateTime =
                LocalDateTime.of(endDate, endTime);

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