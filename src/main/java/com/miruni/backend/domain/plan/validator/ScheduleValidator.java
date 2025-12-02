package com.miruni.backend.domain.plan.validator;

import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class ScheduleValidator {
    private final AiPlanRepository aiPlanRepository;

    public void validateConflict(Long userId, LocalDate date, LocalTime startTime, LocalTime endTime) {

        // 일반 일정 중복 검증

        if(aiPlanRepository.existsOverlap(userId, date, startTime, endTime)) {
            throw BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND);
        }
    }

    // 오버로딩
    public void validateConflict(Long userId, Long excludeId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        boolean isConflict;
        if (excludeId == null) {
            isConflict = aiPlanRepository.existsOverlap(userId, date, startTime, endTime);
        }else{
            isConflict = aiPlanRepository.existsOverlapWithinUpdate(userId, excludeId, date, startTime, endTime);
        }

        if(isConflict){
            throw BaseException.type(AiPlanErrorCode.AI_PLAN_CONFLICT);
        }
    }
}
