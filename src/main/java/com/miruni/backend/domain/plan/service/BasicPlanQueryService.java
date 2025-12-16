package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class BasicPlanQueryService {

    private final BasicPlanRepository basicPlanRepository;
    private final UserQueryService userQueryService;

    public BasicPlan getByPlanIdAndUserId(Long planId, Long userId) {
        userQueryService.getUserById(userId);

        return basicPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> BaseException.type(BasicPlanErrorCode.BASIC_PLAN_NOT_FOUND));
    }
    public boolean isScheduledTimeConflict(Long userId, LocalTime scheduledTime) {
        userQueryService.getUserById(userId);
        return basicPlanRepository.existsByUserIdAndScheduledStartTime(userId, scheduledTime);
    }
}