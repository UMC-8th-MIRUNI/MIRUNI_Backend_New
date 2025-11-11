package com.miruni.backend.domain.question.service;

import com.miruni.backend.domain.question.dto.response.GetListQuestionResponseDto;
import com.miruni.backend.domain.question.dto.response.GetQuestionResponseDto;
import com.miruni.backend.domain.question.dto.response.QuestionQueryResponseDto;
import com.miruni.backend.domain.question.entity.Question;
import com.miruni.backend.domain.question.exception.QuestionErrorCode;
import com.miruni.backend.domain.question.repository.QuestionRepository;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionQueryService {
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public GetQuestionResponseDto getQuestion(Long questionId){
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> BaseException.type(QuestionErrorCode.QUESTION_NOT_FOUND));

        return GetQuestionResponseDto.from(question);
    }

    @Transactional(readOnly = true)
    public GetListQuestionResponseDto getListQuestion(){
        List<Question> questions = questionRepository.findByUserId(1L);

        return GetListQuestionResponseDto.of(
                questions.stream().map(QuestionQueryResponseDto::from)
                        .toList());
    }
}

