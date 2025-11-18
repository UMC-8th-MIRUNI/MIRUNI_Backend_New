package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.BasicPlanCreateCommandDto;
import com.miruni.backend.domain.plan.dto.command.BasicPlanUpdateCommandDto;
import com.miruni.backend.domain.plan.dto.response.BasicPlanResponse;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicPlanCommandService {

    private final BasicPlanRepository basicPlanRepository;
    private final UserQueryService userQueryService;
    private final BasicPlanQueryService basicPlanQueryService;

    public BasicPlanResponse createBasicPlan(BasicPlanCreateCommandDto command) {
        User user = userQueryService.getUserById(command.userId());
        BasicPlan plan = BasicPlan.create(
                user,
                command.title(),
                command.description(),
                command.scheduledDate(),
                command.startTime(),
                command.endTime(),
                mapPriority(command.priority())
        );

        basicPlanRepository.save(plan);
        return BasicPlanResponse.from(plan);
    }

    public BasicPlanResponse updateBasicPlan(BasicPlanUpdateCommandDto command) {
        BasicPlan plan = basicPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());

        plan.update(
                command.title(),
                command.description(),
                command.scheduledDate(),
                command.startTime(),
                command.endTime(),
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