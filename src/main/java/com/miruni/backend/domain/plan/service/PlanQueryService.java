package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.request.PlanStartRequest;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
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
            BasicPlan plan = basicPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            expectedDuration = plan.getExpectedDuration();
        } else if (command.planType() == PlanType.AI) {
            AiPlan plan = aiPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            expectedDuration = (long) plan.getExpectedDuration();
        } else {
            throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        return PlanDurationResponse.of(command.planType(), command.planId(), expectedDuration);
    }

}
