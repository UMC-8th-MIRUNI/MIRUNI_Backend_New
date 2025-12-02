package com.miruni.backend.domain.plan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AiPlansDeleteRequest(
        @Schema(description = "삭제할 일정 아이디 리스트")
        @NotEmpty
        List<Long> aiPlansId
) {
}
