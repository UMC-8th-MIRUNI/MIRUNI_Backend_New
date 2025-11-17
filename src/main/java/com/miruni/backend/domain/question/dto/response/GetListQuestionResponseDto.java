package com.miruni.backend.domain.question.dto.response;

import java.util.List;

public record GetListQuestionResponseDto(
        List<QuestionQueryResponseDto> questionList
) {
    public static GetListQuestionResponseDto of(List<QuestionQueryResponseDto> questionQueryResponseDto) {
        return new GetListQuestionResponseDto(questionQueryResponseDto);
    }
}
