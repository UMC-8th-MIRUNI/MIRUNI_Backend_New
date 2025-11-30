package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.PlanFinishCommand;
import com.miruni.backend.domain.plan.dto.response.PlanFinishResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.type.PlanType;
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
public class PlanCommandService {

    private final BasicPlanQueryService basicPlanQueryService;
    private final AiPlanQueryService aiPlanQueryService;
    private final UserQueryService userQueryService;
    private final AiPlanRepository aiPlanRepository;

    public PlanFinishResponse finishPlan(PlanFinishCommand command) {
        User user = userQueryService.getUserById(command.userId());

        int expectedMinutes = parseTimeToMinutes(command.expectedTime());
        int actualMinutes = parseTimeToMinutes(command.actualTime());
        int peanutCount = calculatePeanuts(expectedMinutes, actualMinutes);

       boolean isDone;
        if (command.planType() == PlanType.BASIC) {
            BasicPlan plan = basicPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            plan.setIsDone(true);
            isDone = plan.isDone();
        } else if (command.planType() == PlanType.AI) {
            AiPlan plan = aiPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            plan.setIsDone(true);
            isDone = plan.isDone();
            // 상위 Plan progressRate 갱신
            updateParentPlanProgress(plan.getPlan());
        } else {
            throw BaseException.type(BasicPlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        user.addPeanuts(peanutCount);

        return PlanFinishResponse.of(peanutCount, command.planType(), command.planId(), isDone);
    }

    private void updateParentPlanProgress(Plan parentPlan) {
        List<AiPlan> aiPlans = aiPlanRepository.findByPlanId(parentPlan.getId());

        int total = aiPlans.size();
        int doneCount = (int) aiPlans.stream().filter(AiPlan::isDone).count();

        int progressRate = (total == 0) ? 0 : (doneCount * 100 / total);
        parentPlan.setProgressRate(progressRate);

        if (progressRate == 100) {
            parentPlan.setIsDone(true);
        }
    }

    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private int calculatePeanuts(int expectedMinutes, int actualMinutes) {
        double ratio = (double) actualMinutes / expectedMinutes * 100;

        if (ratio < 30) return 0;
        if (ratio < 65) return 1;
        if (ratio < 100) return 2;
        return 3;
    }
}
