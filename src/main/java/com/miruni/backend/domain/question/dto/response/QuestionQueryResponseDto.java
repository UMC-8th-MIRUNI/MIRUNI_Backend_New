package com.miruni.backend.domain.question.dto.response;

import com.miruni.backend.domain.question.entity.Question;

import java.time.LocalDateTime;

public record QuestionQueryResponseDto(
        Long id,

        String title,

        LocalDateTime createdAt,

        boolean isAnswer
) {
    public static QuestionQueryResponseDto from(Question question) {

        return new QuestionQueryResponseDto(
                question.getId(),
                question.getTitle(),
                question.getCreatedAt(),
                question.isAnswered()
        );
    }
}
