package com.miruni.backend.domain.plan.dto.command;

import com.miruni.backend.domain.plan.dto.request.AiPlansDeleteRequest;

import java.util.List;

public record AiPlansDeleteCommandDto(
        Long planId,
        List<Long> aiPlansId,
        Long userId
) {
    public static  AiPlansDeleteCommandDto from(Long planId, AiPlansDeleteRequest req, Long userId) {
        return new AiPlansDeleteCommandDto(
                planId,
                req.aiPlansId(),
                userId
        );
    }
}
