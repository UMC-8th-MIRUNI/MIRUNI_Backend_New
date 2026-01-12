package com.miruni.backend.domain.plan.validator;

import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class ScheduleValidator {
    private final AiPlanRepository aiPlanRepository;
    private final BasicPlanRepository basicPlanRepository;

    public void validateConflict(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (basicPlanRepository.existsOverlap(userId, startDateTime, endDateTime)) {
            throw BaseException.type(BasicPlanErrorCode.BASIC_PLAN_CONFLICT);
        }

//        if (aiPlanRepository.existsOverlap(userId, startDateTime, endDateTime)) {
//            throw BaseException.type(AiPlanErrorCode.AI_PLAN_CONFLICT);
//        }
    }

    // 오버로딩

    //BasicPlan
    public void validateConflictForBasicPlan(Long userId, Long excludeId, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        // BasicPlan 검증
        if (basicPlanRepository.existsOverlapWithinUpdate(userId, excludeId, startDateTime, endDateTime)) {
            throw BaseException.type(BasicPlanErrorCode.BASIC_PLAN_CONFLICT);
        }

        // AiPlan 검증
//        if (aiPlanRepository.existsOverlap(userId, startDateTime, endDateTime)) {
//            throw BaseException.type(AiPlanErrorCode.AI_PLAN_CONFLICT);
//        }
    }

    //AiPlan
    public void validateConflictForAiPlan(Long userId, Long excludeId, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        // BasicPlan 검증
        if (basicPlanRepository.existsOverlap(userId, startDateTime, endDateTime)) {
            throw BaseException.type(BasicPlanErrorCode.BASIC_PLAN_CONFLICT);
        }

        // AiPlan 검증
//        if (aiPlanRepository.existsOverlapWithinUpdate(userId, excludeId, startDateTime, endDateTime)) {
//            throw BaseException.type(AiPlanErrorCode.AI_PLAN_CONFLICT);
//        }
    }

}