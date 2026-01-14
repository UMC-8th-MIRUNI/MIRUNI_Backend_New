package com.miruni.backend.domain.plan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanPreviewDto(
        @NotNull
        @Schema(description = "상위 일정 ID", example = "1")
        Long planId,

        @NotBlank
        @Schema(description = "상위 일정 제목", example = "UMC 기획안 만들기")
        String title,

        @Schema(description = "완료한 일정 갯수", example = "3")
        int doneCnt,

        @Schema(description = "총 분할 일정 갯수", example = "10")
        int totalCnt,

        @Schema(description = "진행률", example = "30")
        int progressRate,

        @Schema(description = "완료여부", example = "true")
        boolean isDone
) {
    public static PlanPreviewDto of(Long planId, String title, int doneCnt, int totalCnt, int progressRate, boolean isDone) {
        return new PlanPreviewDto(planId, title, doneCnt, totalCnt, progressRate, isDone);
    }
}
