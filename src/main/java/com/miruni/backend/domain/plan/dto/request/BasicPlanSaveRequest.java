package com.miruni.backend.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BasicPlanSaveRequest(

        @NotBlank(message = "제목은 필수입니다.")
        @Schema(example = "제목")
        String title,

        @NotBlank(message = "설명은 필수입니다.")
        @Schema(example = "설명입니다")
        String description,

        @NotNull(message = "시작 날짜는 필수입니다.")
        @Schema(description = "시작 날짜(00:00 ~ 23:59 사이만 입력 가능)", example = "2025-11-20", type = "string", format = "date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @Schema(description = "종료 날짜(없으면 단일 일정, 00:00 ~ 23:59 사이만 입력 가능)", example = "2025-11-22", nullable = true, type = "string", format = "date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @NotNull(message = "시작 시간은 필수입니다.")
        @NotNull
        @Schema(description = "시작 시간", example = "23:00", type = "string", format = "time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "종료 시간은 필수입니다.")
        @Schema(description = "종료 시간", example = "00:30", type = "string", format = "time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        @NotBlank(message = "우선 순위는 필수입니다.")
        @Schema(example = "상")
        String priority
) {
}