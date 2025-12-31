package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.BasicPlanCreateCommand;
import com.miruni.backend.domain.plan.dto.command.BasicPlanUpdateCommand;
import com.miruni.backend.domain.plan.dto.response.BasicPlanResponse;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.domain.plan.validator.ScheduleValidator;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicPlanCommandService {

    private final BasicPlanRepository basicPlanRepository;
    private final UserQueryService userQueryService;
    private final BasicPlanQueryService basicPlanQueryService;
    private final ScheduleValidator scheduleValidator;

    public List<BasicPlanResponse> createBasicPlan(BasicPlanCreateCommand command) {

        User user = userQueryService.getUserById(command.userId());
        List<BasicPlan> plans = new ArrayList<>();

        LocalDate startDate = command.startDate();
        LocalDate endDate = command.endDate(); // nullable
        LocalTime startTime = command.startTime();
        LocalTime endTime = command.endTime();

        boolean crossMidnight = endTime.isBefore(startTime);

        //단일 일정
        if (endDate == null) {
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(
                    crossMidnight ? startDate.plusDays(1) : startDate,
                    endTime
            );

            scheduleValidator.validateConflict(user.getId(), startDateTime, endDateTime);

            plans.add(BasicPlan.create(
                    user,
                    command.title(),
                    command.description(),
                    startDateTime,
                    endDateTime,
                    mapPriority(command.priority())
            ));

        } else {
            //기간 일정
            LocalDate current = startDate;

            while (!current.isAfter(endDate)) {
                LocalDateTime dayStart = LocalDateTime.of(current, startTime);
                LocalDateTime dayEnd = LocalDateTime.of(
                        crossMidnight ? current.plusDays(1) : current,
                        endTime
                );
                scheduleValidator.validateConflict(user.getId(), dayStart, dayEnd);

                plans.add(BasicPlan.create(
                        user,
                        command.title(),
                        command.description(),
                        dayStart,
                        dayEnd,
                        mapPriority(command.priority())
                ));

                current = current.plusDays(1);
            }
        }

        // 검증 모두 통과하면 한 번에 저장
        basicPlanRepository.saveAll(plans);

        return plans.stream()
                .map(BasicPlanResponse::from)
                .toList();
    }

    public BasicPlanResponse updateBasicPlan(BasicPlanUpdateCommand command) {
        BasicPlan plan = basicPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());

        scheduleValidator.validateConflictForBasicPlan(
                command.userId(),
                command.planId(),
                command.startDateTime(),
                command.endDateTime()
        );

        plan.update(
                command.title(),
                command.description(),
                command.startDateTime(),
                command.endDateTime(),
                mapPriority(command.priority())
        );
        return BasicPlanResponse.from(plan);
    }

    public Long deleteBasicPlan(Long userId, Long planId) {
        BasicPlan plan = basicPlanQueryService.getByPlanIdAndUserId(planId, userId);
        basicPlanRepository.delete(plan);
        return planId;
    }

    private Priority mapPriority(String priorityStr) {
        return switch (priorityStr) {
            case "상" -> Priority.HIGH;
            case "중" -> Priority.MEDIUM;
            case "하" -> Priority.LOW;
            default -> throw BaseException.type(BasicPlanErrorCode.INVALID_PRIORITY_VALUE);
        };
    }
}