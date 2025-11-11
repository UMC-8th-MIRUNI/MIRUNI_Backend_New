package com.miruni.backend.domain.question.controller;

import com.miruni.backend.domain.question.dto.request.SaveQuestionRequestDto;
import com.miruni.backend.domain.question.dto.response.QuestionCommandResponseDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionApi {

    QuestionCommandResponseDto save(
            @Valid @RequestPart("request") SaveQuestionRequestDto saveQuestionRequestDto,
            @RequestPart(value = "question_images", required = false) List<MultipartFile> questionImages);
}
