package com.miruni.backend.domain.question.dto.response;

import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.domain.question.entity.QuestionImage;

import java.time.LocalDateTime;
import java.util.List;

public record GetQuestionResponseDto(
        Long id,

        String title,

        String content,

        List<String> imageUrl,

        List<Long> imageIds,

        LocalDateTime createdAt
) {
    public static GetQuestionResponseDto from(Question question) {

        List<String> imageUrls = question.getImages().stream()
                .map(QuestionImage::getImageUrl)
                .toList();

        List<Long> imageIds = question.getImages().stream()
                .map(QuestionImage::getId)
                .toList();

        return new GetQuestionResponseDto(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                imageUrls,
                imageIds,
                question.getCreatedAt());
    }
}
