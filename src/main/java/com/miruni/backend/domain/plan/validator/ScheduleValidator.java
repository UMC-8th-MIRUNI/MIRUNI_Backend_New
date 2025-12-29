package com.miruni.backend.domain.plan.validator;

import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class ScheduleValidator {
    private final AiPlanRepository aiPlanRepository;
    private final BasicPlanRepository basicPlanRepository;

    public void validateConflict(Long userId, LocalDate date, LocalTime startTime, LocalTime endTime) {

        if (basicPlanRepository.existsOverlap(userId, date, startTime, endTime)) {
            throw BaseException.type(BasicPlanErrorCode.BASIC_PLAN_CONFLICT);
        }

        if(aiPlanRepository.existsOverlap(userId, date, startTime, endTime)) {
            throw BaseException.type(AiPlanErrorCode.AI_PLAN_CONFLICT);
        }
    }

    // 오버로딩
    public void validateConflict(Long userId, Long excludeId, LocalDate date, LocalTime startTime, LocalTime endTime) {

        // BasicPlan 검증
        if (basicPlanRepository.existsOverlapWithinUpdate(userId, excludeId, date, startTime, endTime)) {
            throw BaseException.type(BasicPlanErrorCode.BASIC_PLAN_CONFLICT);
        }

        // AiPlan 검증
        if (aiPlanRepository.existsOverlapWithinUpdate(userId, excludeId, date, startTime, endTime)) {
            throw BaseException.type(AiPlanErrorCode.AI_PLAN_CONFLICT);
        }
    }
}