package com.miruni.backend.domain.plan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AiPlansDeleteResponse(
        @Schema(description = "삭제 성공 여부", example = "true")
        boolean isDeleted
) {
}
