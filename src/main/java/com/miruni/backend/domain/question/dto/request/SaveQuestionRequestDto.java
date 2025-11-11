package com.miruni.backend.domain.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveQuestionRequestDto(

        @NotBlank
        @Schema(example = "앱 사용중 꺼짐")
        String title,

        @NotBlank
        @Schema(example = "알람 아이콘을 누를 시에 자꾸 꺼집니다")
        String content,

        @NotNull
        @Schema(example = "true")
        boolean privacyAgreed
) {
}
