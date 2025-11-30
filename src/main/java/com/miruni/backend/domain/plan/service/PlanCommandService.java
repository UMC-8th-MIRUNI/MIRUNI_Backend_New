package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.PlanFinishCommand;
import com.miruni.backend.domain.plan.dto.command.PlanPauseCommand;
import com.miruni.backend.domain.plan.dto.response.PlanFinishResponse;
import com.miruni.backend.domain.plan.dto.response.PlanPauseResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.exception.PlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
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
       switch (command.planType()) {
           case BASIC -> {
               BasicPlan plan = getBasicPlan(command.planId(), command.userId());
               plan.setIsDone(true);
               isDone = plan.isDone();
           }
           case AI -> {
               AiPlan plan = getAiPlan(command.planId(), command.userId());
               plan.setIsDone(true);
               isDone = plan.isDone();

               // 상위 Plan progressRate 갱신
               updateParentPlanProgress(plan.getPlan());
           }
           default -> throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
       }

        user.addPeanuts(peanutCount);

        return PlanFinishResponse.of(peanutCount, command.planType(), command.planId(), isDone);
    }

    public PlanPauseResponse pausePlan(PlanPauseCommand command) {
        LocalTime newScheduledTime = LocalTime.parse(command.resumeTime());

        boolean isConflict = basicPlanQueryService.isScheduledTimeConflict(command.userId(), newScheduledTime)
                || aiPlanQueryService.isScheduledTimeConflict(command.userId(), newScheduledTime);

        if (isConflict) {
            throw BaseException.type(PlanErrorCode.PLAN_CONFLICT);
        }

        switch (command.planType()) {
            case BASIC -> {
                BasicPlan plan = getBasicPlan(command.planId(), command.userId());
                plan.setScheduledTime(newScheduledTime);
            }
            case AI -> {
                AiPlan plan = getAiPlan(command.planId(), command.userId());
                plan.setScheduledTime(newScheduledTime);
            }
            default -> throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        return PlanPauseResponse.of(command.planType(), command.planId(), command.resumeTime());
    }

    private BasicPlan getBasicPlan(Long planId, Long userId) {
        return basicPlanQueryService.getByPlanIdAndUserId(planId, userId);
    }

    private AiPlan getAiPlan(Long planId, Long userId) {
        return aiPlanQueryService.getByPlanIdAndUserId(planId, userId);
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
