package com.miruni.backend.domain.question.controller;

import com.miruni.backend.domain.question.dto.command.SaveQuestionCommandDto;
import com.miruni.backend.domain.question.dto.request.SaveQuestionRequestDto;
import com.miruni.backend.domain.question.dto.response.QuestionResponseDto;
import com.miruni.backend.domain.question.service.QuestionCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/questions")
public class QuestionController implements QuestionApi {

    private final QuestionCommandService questionCommandService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public QuestionResponseDto save(
            @Valid @RequestPart("request") SaveQuestionRequestDto saveQuestionRequestDto,
            @RequestPart(value = "question_images", required = false)List<MultipartFile> questionImages) {

        return questionCommandService.save(SaveQuestionCommandDto.of(saveQuestionRequestDto, questionImages));
    }
}
