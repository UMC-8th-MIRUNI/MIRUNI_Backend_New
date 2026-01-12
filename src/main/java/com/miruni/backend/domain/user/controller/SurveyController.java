package com.miruni.backend.domain.user.controller;

import com.miruni.backend.domain.user.dto.request.SurveyRequest;
import com.miruni.backend.domain.user.dto.response.SurveyResponse;
import com.miruni.backend.domain.user.dto.response.UserSurveyResponse;
import com.miruni.backend.domain.user.service.UserCommandService;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.authroize.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController implements SurveyApi {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @Override
    @GetMapping
    public UserSurveyResponse getUserSurveyResult(@LoginUser Long userId) {
        return userQueryService.getUserSurveyResult(userId);
    }

    @Override
    @PatchMapping
    public SurveyResponse updateSurvey(@LoginUser Long userId, @RequestBody @Valid SurveyRequest request) {
        return userCommandService.updateSurvey(request, userId);
    }
}

