package com.miruni.backend.domain.question.controller;

import com.miruni.backend.domain.question.dto.request.SaveQuestionRequestDto;
import com.miruni.backend.domain.question.dto.request.UpdateQuestionRequestDto;
import com.miruni.backend.domain.question.dto.response.GetListQuestionResponseDto;
import com.miruni.backend.domain.question.dto.response.GetQuestionResponseDto;
import com.miruni.backend.domain.question.dto.response.QuestionCommandResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Question", description = "문의 관련 API")
public interface QuestionApi {

    @Operation(
            summary = "문의 생성",
            description = "문의 생성 API 입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 등록 성공"),
            @ApiResponse(responseCode = "500", description = "업로드 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    QuestionCommandResponseDto save(
            @Valid @RequestPart("request") SaveQuestionRequestDto saveQuestionRequestDto,
            @RequestPart(value = "question_images", required = false) List<MultipartFile> questionImages);

    @Operation(
            summary = "문의 업데이트",
            description = "문의 업데이트 API 입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 업데이트 성공"),
            @ApiResponse(responseCode = "500", description = "업로드 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    QuestionCommandResponseDto update(
            @Valid @RequestPart("request") UpdateQuestionRequestDto request,
            @RequestPart(value = "questionImages", required = false)List<MultipartFile> questionImages,
            @PathVariable Long questionId
    );

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 리스트 조회 성공"),
    })
    @Operation(
            summary = "문의 리스트 조회",
            description = "문의 리스트 조회 API 입니다."
    )
    GetListQuestionResponseDto getListQuestion();

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 상세 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @Operation(
            summary = "문의 개별 조회",
            description = "문의 개별조회 API 입니다."
    )
    GetQuestionResponseDto getQuestion(@PathVariable Long questionId);
}


