package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.PlanUpdateCommandDto;
import com.miruni.backend.domain.plan.dto.response.AiPlanResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanTableDto;
import com.miruni.backend.domain.plan.dto.response.PlanDeleteAllResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.exception.PlanErrorCode;
import com.miruni.backend.domain.plan.repository.PlanRepository;
import com.miruni.backend.domain.plan.validator.ScheduleValidator;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanCommandService {
    private final PlanQueryService planQueryService;
    private final PlanRepository planRepository;
    private final ScheduleValidator scheduleValidator;

    private static void checkWithinDeadline(LocalDate deadline, LocalDate scheduledDate) {
        if (scheduledDate.isAfter(deadline)) {
            throw BaseException.type(AiPlanErrorCode.DEADLINE_AFTER);
        }
    }

    private static void checkExpectedDuration(LocalTime startTime, LocalTime endTime, int duration) {
        if (duration != Duration.between(startTime, endTime).toMinutes()) {
            throw BaseException.type(AiPlanErrorCode.INVALID_TIME_DURATION);
        }
    }

    public PlanDeleteAllResponse deletePlanAll(Long userId, Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));

        if(!plan.getUser().getId().equals(userId)) {
            throw BaseException.type(CommonErrorCode.FORBIDDEN);
        }

        planRepository.delete(plan);

        return new PlanDeleteAllResponse(true);
    }

    public AiPlanResponse updatePlanTable(PlanUpdateCommandDto command){
        Plan plan = planRepository.findById(command.planId()).orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));

        if(!plan.getUser().getId().equals(command.userId())){
            throw BaseException.type(CommonErrorCode.FORBIDDEN);
        }

        if(command.title() != null && !command.title().isEmpty()){plan.updateTitle(command.title());}
        if(command.deadline() != null) {plan.updateDeadline(command.deadline());}
        if(command.taskRange() != null && !command.taskRange().isEmpty()) {plan.updateScope(command.taskRange());}
        if(command.priority() != null) {plan.updatePriority(command.priority());}

        //하드코드(임시방편)
        int progressRate = 30;

        List<AiPlanTableDto> dtos = command.aiPlans();
        if(dtos != null && !dtos.isEmpty()){
            Map<Long, AiPlan> aiPlanMap = plan.getAiPlans().stream()
                    .collect((Collectors.toMap(AiPlan::getId, Function.identity())));

            for (AiPlanTableDto dto : dtos){
                AiPlan aiPlanKey = aiPlanMap.get(dto.aiPlanId());

                if (aiPlanKey == null){
                    throw BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND);
                }

                checkWithinDeadline(plan.getDeadline().toLocalDate(), dto.scheduledDate());
                checkExpectedDuration(dto.startTime(), dto.endTime(), dto.expectedDuration());
                scheduleValidator.validateConflict(command.userId(), dto.aiPlanId(), dto.scheduledDate(), dto.startTime(), dto.endTime());

                aiPlanKey.updateDetails(
                        dto.subTitle(),
                        dto.scheduledDate(),
                        dto.startTime(),
                        dto.endTime(),
                        dto.expectedDuration()
                );
            }

        }
        List<AiPlanTableDto> savedDtos = plan.getAiPlans().stream()
                .map(aiPlan -> AiPlanTableDto.of(
                        aiPlan.getId(),
                        aiPlan.getScheduledDate(),
                        aiPlan.getScheduledTime(),
                        aiPlan.getEndTime(),
                        aiPlan.getSubTitle(),
                        aiPlan.getExpectedDuration()
                )).toList();

        return AiPlanResponse.of(command.planId(), plan.getTitle(), plan.getDeadline().toLocalDate(), plan.getScope(), plan.getPriority(), progressRate, savedDtos);
    }
}
