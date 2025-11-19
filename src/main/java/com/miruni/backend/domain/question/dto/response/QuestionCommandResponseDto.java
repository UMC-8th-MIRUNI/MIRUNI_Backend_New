package com.miruni.backend.domain.question.dto.response;

import com.miruni.backend.domain.question.entity.Question;

import java.time.LocalDateTime;

public record QuestionCommandResponseDto(
        Long id,

        String title,

        LocalDateTime createdAt

) {
    public static QuestionCommandResponseDto from(Question question) {
        return new QuestionCommandResponseDto(
                question.getId(),
                question.getTitle(),
                question.getCreatedAt());
    }
}
