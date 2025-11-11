package com.miruni.backend.domain.question.dto.response;

import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.domain.question.entity.QuestionImage;

import java.util.List;

public record QuestionResponseDto(
        String title,

        String content,

        List<String> imageUrl
) {
    public static QuestionResponseDto from(Question question) {

        List<String> imageUrls = question.getImages().stream()
                .map(QuestionImage::getImageUrl)
                .toList();

        return new QuestionResponseDto(question.getTitle(), question.getContent(), imageUrls);
    }
}
