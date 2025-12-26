package com.miruni.backend.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "설문조사 수정/완료 응답")
public record SurveyResponse(
        @Schema(description = "메시지", example = "설문조사가 수정되었습니다!")
        String message,

        @Schema(description = "설문 완료/수정 시각")
        LocalDateTime completedAt,

        @Schema(description = "상태", example = "UPDATED")
        String status
) {
    public static SurveyResponse of(String message, LocalDateTime completedAt, String status) {
        return new SurveyResponse(message, completedAt, status);
    }
}


