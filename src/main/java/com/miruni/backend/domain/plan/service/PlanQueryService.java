package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
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

    public PlanDurationResponse getExpectedDuration(PlanDurationCommand command) {
        Long expectedDuration;

        if ("BASIC".equalsIgnoreCase(command.planType())) {
            BasicPlan basicPlan = basicPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            expectedDuration = basicPlan.getExpectedDuration();
        } else if ("AI".equalsIgnoreCase(command.planType())) {
            AiPlan aiPlan = aiPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            expectedDuration = (long) aiPlan.getExpectedDuration();
        } else {
            throw BaseException.type(BasicPlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        return PlanDurationResponse.of(command.planType(), command.planId(), expectedDuration);
    }
}
