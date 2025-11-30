package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AiPlanQueryService {
    private final AiPlanRepository aiPlanRepository;
    private final UserQueryService userQueryService;

    public AiPlan getByPlanIdAndUserId(Long planId, Long userId) {
        userQueryService.getUserById(userId); // 사용자 존재 여부 확인

        return aiPlanRepository.findByIdAndPlanUserId(planId, userId)
                .orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND));
    }
    public boolean isScheduledTimeConflict(Long userId, LocalTime scheduledTime) {
        userQueryService.getUserById(userId);
        return aiPlanRepository.existsByPlanUserIdAndScheduledTime(userId, scheduledTime);
    }
}
