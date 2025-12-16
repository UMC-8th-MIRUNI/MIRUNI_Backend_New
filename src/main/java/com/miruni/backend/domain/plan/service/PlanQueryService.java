package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Status;
import com.miruni.backend.domain.plan.exception.PlanErrorCode;
import com.miruni.backend.domain.plan.type.PlanType;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanQueryService {
    private final BasicPlanQueryService basicPlanQueryService;
    private final AiPlanQueryService aiPlanQueryService;

    @Transactional
    public PlanDurationResponse getExpectedDuration(PlanDurationCommand command) {
        Long expectedDuration;

        if (command.planType() == PlanType.BASIC) {
            BasicPlan basicPlan = basicPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            basicPlan.start();
            expectedDuration = basicPlan.getExpectedDuration();
        } else if (command.planType() == PlanType.AI) {
            AiPlan aiPlan = aiPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            aiPlan.start();
            expectedDuration = (long) aiPlan.getExpectedDuration();
        } else {
            throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        return PlanDurationResponse.of(command.planType(), command.planId(), expectedDuration, Status.IN_PROGRESS);
    }
}
