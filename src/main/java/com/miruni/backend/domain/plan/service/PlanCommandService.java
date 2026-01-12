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
        int doneCount = (int) aiPlans.stream().filter(aiPlan -> aiPlan.getStatus() == Status.DONE).count();

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
                scheduleValidator.validateConflictForAiPlan(command.userId(), dto.aiPlanId(),
                        dto.scheduledDate().atTime(dto.startTime()),
                        dto.scheduledDate().atTime(dto.endTime())
                );

                aiPlanKey.updateDetails(
                        dto.subTitle(),
                        dto.scheduledDate().atTime(dto.startTime()),
                        dto.scheduledDate().atTime(dto.endTime()),
                        dto.expectedDuration(),
                        dto.status()
                );
            }

        }
        List<AiPlanTableDto> savedDtos = plan.getAiPlans().stream()
                .map(aiPlan -> AiPlanTableDto.of(
                        aiPlan.getId(),
                        aiPlan.getStartDateTime().toLocalDate(),
                        aiPlan.getStartDateTime().toLocalTime(),
                        aiPlan.getEndDateTime().toLocalTime(),
                        aiPlan.getSubTitle(),
                        aiPlan.getExpectedDuration(),
                        aiPlan.getStatus()
                )).toList();

        return AiPlanResponse.of(command.planId(), plan.getTitle(), plan.getDeadline().toLocalDate(), plan.getScope(), plan.getPriority(), plan.getProgressRate(), savedDtos);
    }

    public PlanStartResponse startPlan(PlanStartRequest request) {
        LocalDateTime now = LocalDateTime.now();
        int durationMinutes = parseTimeToMinutes(request.durationStr());
        LocalDateTime endDateTime = now.plusMinutes(durationMinutes);

        if (request.planType() == PlanType.BASIC) {
            scheduleValidator.validateConflictForBasicPlan(request.userId(), request.planId(), now, endDateTime);
        } else if (request.planType() == PlanType.AI) {
            scheduleValidator.validateConflictForAiPlan(request.userId(), request.planId(), now, endDateTime);
        }

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
        int actualMinutes;


        Status status;
       switch (command.planType()) {
           case BASIC -> {
               BasicPlan basicPlan = getBasicPlan(command.planId(), command.userId());
               actualMinutes = (int) Duration.between(basicPlan.getUpdatedAt(), LocalDateTime.now()).toMinutes();
               basicPlan.complete();
               status = basicPlan.getStatus();
           }
           case AI -> {
               AiPlan aiPlan = getAiPlan(command.planId(), command.userId());
               actualMinutes = (int) Duration.between(aiPlan.getUpdatedAt(), LocalDateTime.now()).toMinutes();

               aiPlan.complete();
               status = aiPlan.getStatus();

                // 상위 Plan progressRate 갱신
                updateParentPlanProgress(aiPlan.getPlan());
            }
            default -> throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        int peanutCount = calculatePeanuts(expectedMinutes, actualMinutes);
        user.addPeanuts(peanutCount);

        return PlanFinishResponse.of(peanutCount, command.planType(), command.planId(), status);
    }

    public PlanPauseResponse pausePlan(PlanPauseCommand command) {
        LocalTime newScheduledTime = LocalTime.parse(command.resumeTime());
        LocalDate today = LocalDate.now();

        long expectedDurationMinutes;
        switch (command.planType()) {
            case BASIC -> {
                BasicPlan plan = getBasicPlan(command.planId(), command.userId());
                expectedDurationMinutes = plan.getExpectedDuration();

                LocalDateTime startDateTime = LocalDateTime.of(today, newScheduledTime);
                LocalDateTime endDateTime = startDateTime.plusMinutes(expectedDurationMinutes);

                if (!endDateTime.isAfter(startDateTime)) {
                    endDateTime = endDateTime.plusDays(1);
                }

                scheduleValidator.validateConflictForBasicPlan(
                        command.userId(),
                        command.planId(),
                        startDateTime,
                        endDateTime
                );
                plan.pause();
                plan.rescheduleTime(startDateTime);
            }
            case AI -> {
                AiPlan plan = getAiPlan(command.planId(), command.userId());
                expectedDurationMinutes = (long) plan.getExpectedDuration();

                LocalDateTime startDateTime = LocalDateTime.of(today, newScheduledTime);
                LocalDateTime endDateTime = startDateTime.plusMinutes(expectedDurationMinutes);

                if (!endDateTime.isAfter(startDateTime)) {
                    endDateTime = endDateTime.plusDays(1);
                }
                scheduleValidator.validateConflictForAiPlan(
                        command.userId(),
                        command.planId(),
                        startDateTime,
                        endDateTime
                );
                plan.pause();
                plan.rescheduleTime(startDateTime);
            }
            default -> throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        return PlanPauseResponse.of(command.planType(), command.planId(), command.resumeTime());
    }

}
