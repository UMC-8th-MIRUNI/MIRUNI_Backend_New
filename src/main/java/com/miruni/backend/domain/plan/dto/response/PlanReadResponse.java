package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.Plan;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlanReadResponse(
        @Schema(description = "ai 플래닝 잔여 횟수", example = "3")
        int remainingAiCnt,

        @NotNull
        @Schema(description = "상위 일정 리스트")
        List<PlanPreviewDto> plans
) {
    public static PlanReadResponse of(int remainingAiCnt, List<PlanPreviewDto> plans) {
        return new PlanReadResponse(remainingAiCnt, plans);
    }
}
