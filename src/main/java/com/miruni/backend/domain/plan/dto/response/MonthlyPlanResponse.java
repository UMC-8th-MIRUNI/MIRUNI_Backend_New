package com.miruni.backend.domain.plan.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MonthlyPlanResponse(
        LocalDateTime dateTime,
        long unfinishedPlanCount
) {
    public static MonthlyPlanResponse of(LocalDateTime dateTime, long count) {
        return new MonthlyPlanResponse(
                dateTime,
                count
        );
    }
}
