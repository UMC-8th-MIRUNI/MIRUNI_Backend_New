package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.response.PlanPreviewDto;
import com.miruni.backend.domain.plan.dto.response.PlanReadResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.exception.PlanErrorCode;
import com.miruni.backend.domain.plan.repository.PlanRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanQueryService {
    private final PlanRepository planRepository;
    private final UserQueryService userQueryService;

    public Plan findById(Long planId) {
        return planRepository.findById(planId).orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));
    }

    public PlanReadResponse findPlans(Long userId) {
        User user = userQueryService.getUserById(userId);
        int remainingCnt = user.getRemainChance();

        List<Plan> plans = planRepository.findAllByUserId(userId);
        List<PlanPreviewDto> planDtos = plans.stream()
                .map(plan -> {
                    List<AiPlan> aiPlans = plan.getAiPlans();

                    int totalCnt = aiPlans.size();
                    int doneCnt = (int) aiPlans.stream()
                            .filter(AiPlan::isDone)
                            .count();

                    // 임시 방안
                    int progressRate = (totalCnt == 0) ? 0 : (int) ((double) doneCnt / totalCnt * 100);

                    boolean isDone = (totalCnt > 0 && doneCnt == totalCnt);

                    return  PlanPreviewDto.of(
                            plan.getId(),
                            plan.getTitle(),
                            doneCnt,
                            totalCnt,
                            progressRate,
                            isDone
                    );
                })
                .toList();

        return PlanReadResponse.of(remainingCnt, planDtos);
    }
}
