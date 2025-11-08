package com.miruni.backend.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record BasicPlanCreateRequest(

        @NotBlank(message = "제목은 필수입니다.")
        @Schema(example = "제목")
        String title,

        @NotBlank(message = "설명은 필수입니다.")
        @Schema(example = "설명입니다")
        String description,

        @NotNull(message = "날짜는 필수입니다.")
        @JsonFormat(pattern = "yyyy.MM.dd")
        @Schema(type = "string", example = "2025.11.08")
        LocalDate scheduledDate,

        @NotNull(message = "시작 시각은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", example = "10:00")
        LocalTime startTime,

        @NotNull(message = "종료 시각은 필수입니다.")
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", example = "11:30")
        LocalTime endTime,

        @NotBlank(message = "우선 순위는 필수입니다.")
        @Schema(example = "상")
        String priority
) {
    public BasicPlan toEntity(User user, Long expectedDuration, Priority mappedPriority) {
        return BasicPlan.builder()
                .title(this.title)
                .description(this.description)
                .scheduledDate(this.scheduledDate)
                .scheduledTime(this.startTime)
                .expectedDuration(expectedDuration)
                .priority(mappedPriority)
                .user(user)
                .build();
    }
}