package com.miruni.backend.domain.question.controller;

import com.miruni.backend.domain.question.dto.command.SaveQuestionCommandDto;
import com.miruni.backend.domain.question.dto.command.UpdateQuestionCommandDto;
import com.miruni.backend.domain.question.dto.request.SaveQuestionRequestDto;
import com.miruni.backend.domain.question.dto.request.UpdateQuestionRequestDto;
import com.miruni.backend.domain.question.dto.response.QuestionCommandResponseDto;
import com.miruni.backend.domain.question.service.QuestionCommandService;
import com.miruni.backend.global.annotation.SwaggerBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/questions")
public class QuestionController implements QuestionApi {

    private final QuestionCommandService questionCommandService;

    @SwaggerBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public QuestionCommandResponseDto save(
            @Valid @RequestPart("request") SaveQuestionRequestDto request,
            @RequestPart(value = "questionImages", required = false)List<MultipartFile> questionImages) {

        return questionCommandService.save(SaveQuestionCommandDto.of(request, questionImages));
    }

    @SwaggerBody(content = @Content(
            encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
    ))
    @PatchMapping(value = {"/{questionId}"}, consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public QuestionCommandResponseDto update(
            @Valid @RequestPart("request") UpdateQuestionRequestDto request,
            @RequestPart(value = "questionImages", required = false)List<MultipartFile> questionImages,
            @PathVariable Long questionId
    ){
        return questionCommandService.update(UpdateQuestionCommandDto.of(request, questionImages, questionId));
    }
}
