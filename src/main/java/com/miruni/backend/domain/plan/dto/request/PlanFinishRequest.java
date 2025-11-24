package com.miruni.backend.domain.plan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "일정 종료 요청 DTO")
public record PlanFinishRequest(
        @Schema(description = "플랜 타입 (BASIC 또는 AI)", example = "BASIC")
        @NotBlank
        String planType,

        @Schema(description = "플랜 ID", example = "10")
        Long id,

        @Schema(description = "예상 수행 시간 HH:mm 형식", example = "01:39")
        @NotBlank
        String expectedTime,

        @Schema(description = "실제 수행 시간 HH:mm 형식", example = "00:20")
        @NotBlank
        String actualTime
) {}
