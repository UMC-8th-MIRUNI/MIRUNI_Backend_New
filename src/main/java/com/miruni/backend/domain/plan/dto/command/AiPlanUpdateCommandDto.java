package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.AiPlanUpdateRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public record AiPlanUpdateCommandDto(
        Long userId,
        Long aiPlanId,
        String title,
        String subTitle,
        LocalDate scheduledDate,
        LocalTime startTime,
        LocalTime endTime
) {
    public static AiPlanUpdateCommandDto from(Long userId, Long aiPlanId, AiPlanUpdateRequest request) {
        return new AiPlanUpdateCommandDto(
                userId,
                aiPlanId,
                request.title(),
                request.subTitle(),
                request.scheduledDate(),
                request.startTime(),
                request.endTime()
        );
    }
}
