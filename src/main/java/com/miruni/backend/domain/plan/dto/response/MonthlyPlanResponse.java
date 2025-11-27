package com.miruni.backend.domain.plan.dto.response;

import java.time.LocalDate;

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
