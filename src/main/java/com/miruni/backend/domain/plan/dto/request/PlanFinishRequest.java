package com.miruni.backend.domain.plan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "일정 종료 요청 DTO")
public record PlanFinishRequest(

        @Schema(description = "실행 예정으로 설정했던 시간 HH:mm 형식", example = "01:39")
        @NotBlank
        String expectedTime
) {}
