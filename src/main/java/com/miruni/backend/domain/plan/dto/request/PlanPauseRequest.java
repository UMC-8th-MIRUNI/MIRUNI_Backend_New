package com.miruni.backend.domain.plan.dto.request;

import com.miruni.backend.domain.plan.type.PlanType;

public record PlanPauseRequest(
        Long planId,
        PlanType planType,
        String resumeTime // "HH:mm" 형식으로 오늘 안에 실행 시간
) {}
