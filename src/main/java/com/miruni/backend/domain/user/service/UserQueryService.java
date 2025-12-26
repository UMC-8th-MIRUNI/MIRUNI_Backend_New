package com.miruni.backend.domain.user.service;

import com.miruni.backend.domain.user.dto.response.UserSurveyResponse;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.ServeyErrorCode;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public UserSurveyResponse getUserSurveyResult(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        if (user.getSurvey() == null) {
            throw BaseException.type(ServeyErrorCode.SURVEY_NOT_FOUND);
        }

        return UserSurveyResponse.fromSurvey(user.getSurvey());
    }
}


