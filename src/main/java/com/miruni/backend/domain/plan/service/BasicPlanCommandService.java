package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;
import com.miruni.backend.domain.plan.dto.response.BasicPlanResponse;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicPlanCommandService {

    private final BasicPlanRepository basicPlanRepository;
    private final UserRepository userRepository;

    public BasicPlanResponse createBasicPlan(Long userId, BasicPlanSaveRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        Priority priority = mapPriority(request.priority());

        BasicPlan plan = BasicPlan.create(user, request.title(), request.description(),
                request.scheduledDate(), request.startTime(), request.endTime(), priority);

        basicPlanRepository.save(plan);
        return BasicPlanResponse.from(plan);
    }

    public BasicPlanResponse updateBasicPlan(Long userId, Long planId, BasicPlanSaveRequest request) {
        BasicPlan plan = basicPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> BaseException.type(BasicPlanErrorCode.BASIC_PLAN_NOT_FOUND));
        plan.update(request.title(), request.description(), request.scheduledDate(),
                request.startTime(), request.endTime(), mapPriority(request.priority()));
        return BasicPlanResponse.from(plan);
    }

    public Long deleteBasicPlan(Long userId, Long planId) {
        BasicPlan plan = basicPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> BaseException.type(BasicPlanErrorCode.BASIC_PLAN_NOT_FOUND));

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