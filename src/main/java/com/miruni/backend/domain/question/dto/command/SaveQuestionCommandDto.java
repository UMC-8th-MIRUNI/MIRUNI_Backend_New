package com.miruni.backend.domain.question.dto.command;

import com.miruni.backend.domain.question.dto.request.SaveQuestionRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record SaveQuestionCommandDto(

        String title,

        String content,

        boolean privacyAgreed,

        List<MultipartFile> imageUrls
) {

    public static SaveQuestionCommandDto of(SaveQuestionRequestDto request, List<MultipartFile> imageUrls) {
        return new SaveQuestionCommandDto(request.title(), request.content(), request.privacyAgreed(), imageUrls);
    }
}
