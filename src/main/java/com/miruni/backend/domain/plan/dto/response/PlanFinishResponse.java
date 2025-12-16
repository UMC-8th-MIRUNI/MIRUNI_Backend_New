package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.Status;
import com.miruni.backend.domain.plan.type.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlanFinishResponse(

    @Schema(description = "획득 땅콩 수", example = "2")
    int peanutCount,

    @Schema(description = "플랜 타입 (BASIC 또는 AI)", example = "BASIC")
    PlanType planType,

    @Schema(description = "플랜 ID", example = "10")
    Long planId,

    @Schema(description = "플랜 상태", example = "DONE")
    Status status
){
    public static PlanFinishResponse of(int peanutCount, PlanType planType, Long planId, Status status) {
        return new PlanFinishResponse(peanutCount, planType, planId, status);
    }
}
