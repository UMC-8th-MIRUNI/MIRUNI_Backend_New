package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.request.BasicPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.response.BasicPlanResponse;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Priority;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicPlanCommandService {

    private final BasicPlanRepository basicPlanRepository;
    private final UserRepository userRepository;

    @Transactional
    public BasicPlanResponse createBasicPlan(Long userId, BasicPlanCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        if (request.startTime().isAfter(request.endTime())) {
            throw BaseException.type(BasicPlanErrorCode.INVALID_TIME_RANGE);
        }

        Priority mappedPriority = mapPriority(request.priority());

        long expectedDuration = java.time.Duration.between(request.startTime(), request.endTime()).toMinutes();

        BasicPlan plan = request.toEntity(user, expectedDuration, mappedPriority);
        BasicPlan savedBasicPlan = basicPlanRepository.save(plan);

        return BasicPlanResponse.from(savedBasicPlan);
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