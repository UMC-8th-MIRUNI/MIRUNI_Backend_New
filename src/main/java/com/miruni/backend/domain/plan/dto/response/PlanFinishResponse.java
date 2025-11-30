package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.type.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanFinishResponse {

    @Schema(description = "획득 땅콩 수", example = "2")
    private final int peanutCount;

    @Schema(description = "플랜 타입 (BASIC 또는 AI)", example = "BASIC")
    private final PlanType planType;

    @Schema(description = "플랜 ID", example = "10")
    private final Long planId;

    @Schema(description = "플랜 완료 여부", example = "true")
    private final boolean isDone;

    public static PlanFinishResponse of(int peanutCount, PlanType planType, Long planId, boolean isDone) {
        return PlanFinishResponse.builder()
                .peanutCount(peanutCount)
                .planType(planType)
                .planId(planId)
                .isDone(isDone)
                .build();
    }
}
