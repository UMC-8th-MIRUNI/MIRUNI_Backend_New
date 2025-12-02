package com.miruni.backend.domain.plan.dto.response;

import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import io.swagger.v3.oas.annotations.media.Schema;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AiPlanUpdateResponse(
        @Schema(description = "상위 일정 제목")
        String title,

        @Schema(description = "하위 일정 제목")
        String subTitle,

        @Schema(description = "실행날짜")
        LocalDate scheduledDate,

        @Schema(description = "실행시간")
        LocalTime startTime,

        @Schema(description = "종료시간")
        LocalTime endTime,

        @Schema(description = "수정시각")
        LocalDateTime updatedAt
) {
        public static  AiPlanUpdateResponse fromEntity(AiPlan aiPlan, Plan plan) {
                return new AiPlanUpdateResponse(
                        plan.getTitle(),
                        aiPlan.getSubTitle(),
                        aiPlan.getScheduledDate(),
                        aiPlan.getEndTime(),
                        aiPlan.getScheduledTime(),
                        aiPlan.getUpdatedAt()
                );
        }
}
