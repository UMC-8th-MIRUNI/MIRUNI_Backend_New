package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.response.AiPlanResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanTableDto;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AiPlanQueryService {
    private final AiPlanRepository aiPlanRepository;
    private final UserQueryService userQueryService;
    private final PlanQueryService planQueryService;

    public AiPlan getByPlanIdAndUserId(Long planId, Long userId) {
        //userQueryService.getUserById(userId);

        return aiPlanRepository.findByIdAndPlanUserId(planId, userId)
                .orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND));
    }
    public boolean isScheduledTimeConflict(Long userId, LocalTime scheduledTime) {
        userQueryService.getUserById(userId);
        return aiPlanRepository.existsByPlanUserIdAndScheduledTime(userId, scheduledTime);
    }

    public AiPlanResponse findAiPlans(Long userId, Long planId) {
        Plan plan = planQueryService.findById(planId);

        if (!plan.getUser().getId().equals(userId)) {
            throw BaseException.type(CommonErrorCode.FORBIDDEN);
        }
        List<AiPlan> aiPlanList = plan.getAiPlans();

        // 진행률 계산 로직(임시)
        int totalCnt = aiPlanList.size();
        int doneCnt = (int) aiPlanList.stream().filter(AiPlan::isDone).count();
        int progressRate = (totalCnt == 0) ? 0 : (int) ((double) doneCnt / totalCnt * 100);

        List<AiPlanTableDto> aiPlanResponses = aiPlanList.stream()
                .map(aiPlan -> {
                    return AiPlanTableDto.of(
                            aiPlan.getId(),
                            aiPlan.getScheduledDate(),
                            aiPlan.getScheduledTime(),
                            aiPlan.getEndTime(),
                            aiPlan.getSubTitle(),
                            aiPlan.getExpectedDuration()
                    );
                }).toList();

            return AiPlanResponse.of(
                    plan.getId(),
                    plan.getTitle(),
                    plan.getDeadline().toLocalDate(),
                    plan.getScope(),
                    plan.getPriority(),
                    progressRate,
                    aiPlanResponses
                    );
    }
}
