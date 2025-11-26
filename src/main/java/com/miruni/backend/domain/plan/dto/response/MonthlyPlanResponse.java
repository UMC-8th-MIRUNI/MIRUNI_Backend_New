package com.miruni.backend.domain.plan.dto.response;

import java.time.LocalDate;

public record MonthlyPlanResponse(
        LocalDate date,
        long unfinishedPlanCount
) {
}
