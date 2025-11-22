package com.miruni.backend.domain.plan.dto.request;

import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.entity.TimePeriod;
import com.miruni.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AiPlanCreateRequest(
        @NotBlank
        @Schema(description = "상위 일정 제목", example = "UMC 기획안 만들기")
        String title,

        @NotNull
        @Schema(description = "마감기한", example = "2025-12-31")
        LocalDate deadline,

        @NotNull
        @Schema(description = "실행시간대", example = "MORNING")
        TimePeriod timePeriod,

        @NotBlank
        @Schema(description = "일정 범위", example = "슬라이드 13장 제작")
        String taskRange,

        @NotNull
        @Schema(description = "우선 순위", example = "HIGH")
        Priority priority,

        @NotBlank
        @Schema(description = "세부 요청사항", example = "하루에 2시간씩 작업할 예정이야.")
        String detailRequest
) {

        public Plan toEntity(User user) {
                return Plan.builder()
                        .title(this.title())
                        .deadline(this.deadline().atStartOfDay())
                        .scope(this.taskRange())
                        .priority(this.priority)
                        .user(user)
                        .build();
        }
}
