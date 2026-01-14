package com.miruni.backend.domain.plan.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MonthlyPlanResponse(
        LocalDate date,
        long unfinishedPlanCount
) {
    public static MonthlyPlanResponse of(LocalDate date, long count) {
        return new MonthlyPlanResponse(
                date,
                count
        );
    }
}
