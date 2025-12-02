package com.miruni.backend.domain.plan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlanDeleteAllResponse(
        @Schema(description = "삭제 완료 여부")
        boolean isDeleted
) {
}
