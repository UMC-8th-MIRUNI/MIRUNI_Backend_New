package com.miruni.backend.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;


@Schema(description = "일반 일정 수정 요청 DTO")
public record BasicPlanUpdateRequest(

        @Schema(description = "일정 제목", example = "팀 회의")
        String title,

        @Schema(description = "일정 설명", example = "주간 진행 상황 공유")
        String description,

        @Schema(description = "일정 날짜 (기준 날짜)", example = "2025-11-20", type = "string", format = "date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "시작 시간", example = "23:00", type = "string", format = "time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @Schema(description = "종료 시간 (시작 시간보다 이르면 다음 날로 자동 처리됨)", example = "00:30", type = "string", format = "time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        @Schema(description = "우선순위 (상 / 중 / 하)", example = "상")
        String priority
) {
}