package com.miruni.backend.domain.question.dto.command;

import com.miruni.backend.domain.question.dto.request.UpdateQuestionRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateQuestionCommandDto(
        Long questionId,

        String title,

        String content,

        List<Long> deleteImageIds,

        List<MultipartFile> imageUrls

) {
    public static UpdateQuestionCommandDto of(UpdateQuestionRequestDto request,
                                              List<MultipartFile> imageUrls,
                                              Long questionId) {
        return new UpdateQuestionCommandDto(
                questionId,
                request.title(),
                request.content(),
                request.deleteImageIds(),
                imageUrls);
    }
}
