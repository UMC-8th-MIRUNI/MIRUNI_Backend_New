package com.miruni.backend.domain.plan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AiPlanDeleteResponse(
        @Schema(description = "해당 일정 삭제 여부")
        boolean isDeleted
) {
}
