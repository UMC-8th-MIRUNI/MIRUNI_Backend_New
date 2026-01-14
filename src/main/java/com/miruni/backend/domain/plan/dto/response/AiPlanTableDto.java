package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AiPlanTableDto(
        @NotNull
        @Schema(description = "세부일정 ID", example = "1")
        Long aiPlanId,

        @NotNull
        @Schema(description = "진행 날짜", example = "2025-07-20")
        LocalDate scheduledDate,

        @NotNull
        @Schema(description = "시작 시간", example = "10:00:00")
        LocalTime startTime,

        @NotNull
        @Schema(description = "종료 시간", example = "10:40:00")
        LocalTime endTime,

        @NotBlank
        @Schema(description = "세부 일정 제목", example = "초안 그리기, 스토리보드 작성")
        String subTitle,

        @Schema(description = "예상 소요 시간", example = "40")
        int expectedDuration,

        @Schema(description = "진행 상태", example = "DONE")
        Status status
) {
    public static AiPlanTableDto of(Long aiPlanId,
                                    LocalDate scheduledDate,
                                    LocalTime startTime, LocalTime endTime,
                                    String subTitle,
                                    int expectedDuration, Status status) {
        return new AiPlanTableDto(aiPlanId, scheduledDate, startTime, endTime, subTitle, expectedDuration, status);
    }
}
