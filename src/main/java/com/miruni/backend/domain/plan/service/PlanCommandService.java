package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.PlanUpdateCommandDto;
import com.miruni.backend.domain.plan.dto.response.AiPlanResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanTableDto;
import com.miruni.backend.domain.plan.dto.response.PlanDeleteAllResponse;
import com.miruni.backend.domain.plan.dto.command.PlanFinishCommand;
import com.miruni.backend.domain.plan.dto.command.PlanPauseCommand;
import com.miruni.backend.domain.plan.dto.request.PlanStartRequest;
import com.miruni.backend.domain.plan.dto.response.PlanFinishResponse;
import com.miruni.backend.domain.plan.dto.response.PlanPauseResponse;
import com.miruni.backend.domain.plan.dto.response.PlanStartResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.entity.Status;
import com.miruni.backend.domain.plan.exception.PlanErrorCode;
import com.miruni.backend.domain.plan.repository.PlanRepository;
import com.miruni.backend.domain.plan.validator.ScheduleValidator;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.type.PlanType;
import com.miruni.backend.domain.plan.validator.ScheduleValidator;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final BasicPlanQueryService basicPlanQueryService;
    private final AiPlanQueryService aiPlanQueryService;
    private final UserQueryService userQueryService;
    private final AiPlanRepository aiPlanRepository;

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
        parentPlan.updateProgressRate(progressRate);

        if (progressRate == 100) {
            parentPlan.complete();
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

    public PlanStartResponse startPlan(PlanStartRequest request) {
        LocalDateTime now = LocalDateTime.now();
        int durationMinutes = parseTimeToMinutes(request.durationStr());
        LocalDateTime endTime = now.plusMinutes(durationMinutes);

        LocalDate date = now.toLocalDate();
        LocalTime startTime = now.toLocalTime();
        LocalTime endTimeOnly = endTime.toLocalTime();

        scheduleValidator.validateConflict(request.userId(), request.planId(), date, startTime, endTimeOnly);

        // 일정 상태 변경
        if (request.planType() == PlanType.BASIC) {
            BasicPlan basicPlan = basicPlanQueryService.getByPlanIdAndUserId(request.planId(), request.userId());
            basicPlan.start();
            return PlanStartResponse.of(PlanType.BASIC,basicPlan.getId(), basicPlan.getStatus());
        } else if (request.planType() == PlanType.AI) {
            AiPlan aiPlan = aiPlanQueryService.getByPlanIdAndUserId(request.planId(), request.userId());
            aiPlan.start();
            return PlanStartResponse.of(PlanType.AI, aiPlan.getId(), aiPlan.getStatus());
        } else {
            throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }
    }

    public PlanFinishResponse finishPlan(PlanFinishCommand command) {
        User user = userQueryService.getUserById(command.userId());

        int expectedMinutes = parseTimeToMinutes(command.expectedTime());
        int actualMinutes = parseTimeToMinutes(command.actualTime());
        int peanutCount = calculatePeanuts(expectedMinutes, actualMinutes);

       Status status;
       switch (command.planType()) {
           case BASIC -> {
               BasicPlan basicplan = getBasicPlan(command.planId(), command.userId());
               basicplan.complete();
               status = basicplan.getStatus();
           }
           case AI -> {
               AiPlan aiPlan = getAiPlan(command.planId(), command.userId());
               aiPlan.complete();
               status = aiPlan.getStatus();

                // 상위 Plan progressRate 갱신
                updateParentPlanProgress(aiPlan.getPlan());
            }
            default -> throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        user.addPeanuts(peanutCount);

        return PlanFinishResponse.of(peanutCount, command.planType(), command.planId(), status);
    }

    public PlanPauseResponse pausePlan(PlanPauseCommand command) {
        LocalTime newScheduledTime = LocalTime.parse(command.resumeTime());

        long expectedDurationMinutes;
        switch (command.planType()) {
            case BASIC -> {
                BasicPlan plan = getBasicPlan(command.planId(), command.userId());
                expectedDurationMinutes = plan.getExpectedDuration();
                scheduleValidator.validateConflict(
                        command.userId(),
                        command.planId(),
                        LocalDate.now(),
                        newScheduledTime,
                        newScheduledTime.plusMinutes(expectedDurationMinutes)
                );
                plan.pause();
                plan.rescheduleTime(newScheduledTime);
            }
            case AI -> {
                AiPlan plan = getAiPlan(command.planId(), command.userId());
                expectedDurationMinutes = (long) plan.getExpectedDuration();
                scheduleValidator.validateConflict(
                        command.userId(),
                        command.planId(),
                        LocalDate.now(),
                        newScheduledTime,
                        newScheduledTime.plusMinutes(expectedDurationMinutes)
                );
                plan.pause();
                plan.rescheduleTime(newScheduledTime);
            }
            default -> throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        return PlanPauseResponse.of(command.planType(), command.planId(), command.resumeTime());
    }

}
