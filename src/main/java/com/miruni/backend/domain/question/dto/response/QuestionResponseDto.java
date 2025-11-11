package com.miruni.backend.domain.question.dto.response;

import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.domain.question.entity.QuestionImage;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionResponseDto(
        Long id,

        String title,

        String content,

        List<String> imageUrl,

        List<Long> imageIds,

        String email,

        LocalDateTime createdAt
) {
    public static QuestionResponseDto from(Question question) {

        List<String> imageUrls = question.getImages().stream()
                .map(QuestionImage::getImageUrl)
                .toList();

        List<Long> imageIds = question.getImages().stream()
                .map(QuestionImage::getId)
                .toList();

        return new QuestionResponseDto(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                imageUrls,
                imageIds,
                question.getUser().getEmail(),
                question.getCreatedAt());
    }
}
