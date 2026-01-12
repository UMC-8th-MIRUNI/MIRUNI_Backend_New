package com.miruni.backend.domain.user.service;

import com.miruni.backend.domain.user.dto.response.UserSurveyResponse;
import com.miruni.backend.domain.user.entity.Survey;
import com.miruni.backend.domain.user.dto.response.UserInfoResponseDto;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.ServeyErrorCode;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.SurveyRepository;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public UserSurveyResponse getUserSurveyResult(Long userId) {
        // 사용자 존재 검증 (Survey만 조회하면 USER_NOT_FOUND 대신 SURVEY_NOT_FOUND가 나갈 수 있어 분리)
        userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        Survey survey = surveyRepository.findByUserId(userId);
        if (survey == null) {
            throw BaseException.type(ServeyErrorCode.SURVEY_NOT_FOUND);
        }

        return UserSurveyResponse.fromSurvey(survey);
    }
}


