package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.response.*;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.domain.plan.type.PlanType;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.exception.PlanErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanQueryService {

    private final AiPlanRepository aiPlanRepository;
    private final BasicPlanRepository basicPlanRepository;
    private final BasicPlanQueryService basicPlanQueryService;
    private final AiPlanQueryService aiPlanQueryService;

    public PlanHomeResponse getPlanHome(Long userId) {
//        LocalDate today = LocalDate.now();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        List<DailyPlanResponse.DailyPlanItemResponse> allPlans =
                Stream.concat(
                        basicPlanRepository.findDailyBasicPlans(userId, today).stream()
                                .map(DailyPlanResponse.DailyPlanItemResponse::fromBasic),
                        aiPlanRepository.findDailyAiPlans(userId, today).stream()
                                .map(DailyPlanResponse.DailyPlanItemResponse::fromAi)
                ).toList();

        int progressRate = calculateProgressRate(allPlans);

        List<DailyPlanResponse.DailyPlanItemResponse> todayPlans = allPlans.stream()
                .filter(p -> !p.isDone())
                .sorted(Comparator.comparing(
                        DailyPlanResponse.DailyPlanItemResponse::scheduledTime
                ))
                .toList();

        return PlanHomeResponse.of(progressRate, todayPlans);
    }

    private int calculateProgressRate(List<DailyPlanResponse.DailyPlanItemResponse> plans) {
        if (plans.isEmpty()) return 0;

        long completedCount = plans.stream()
                .filter(DailyPlanResponse.DailyPlanItemResponse::isDone)
                .count();

        return (int) Math.round(completedCount * 100.0 / plans.size());
    }

    /**
     * 캘린더 조회
     */
    public List<MonthlyPlanResponse> getMonthlyPlan(Long userId, int year, int month) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<MonthlyPlanResponse> basicPlans = basicPlanRepository.countUnfinishedBasicPlansByDate(userId, startDate, endDate);
        List<MonthlyPlanResponse> aiPlans = aiPlanRepository.countUnfinishedAiPlansByDate(userId, startDate, endDate);

        return Stream.concat(basicPlans.stream(), aiPlans.stream())
                .collect(Collectors.groupingBy(
                        MonthlyPlanResponse::date,
                        Collectors.summingLong(MonthlyPlanResponse::unfinishedPlanCount)
                ))
                .entrySet().stream()
                .map(e -> MonthlyPlanResponse.of(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(MonthlyPlanResponse::date))
                .toList();
    }

    /**
     * (캘린더 아래) 특정 날짜의 일정 조회
     */
    public DailyPlanResponse getDailyPlan(Long userId, int year, int month, int day) {

        LocalDate date = LocalDate.of(year, month, day);

        List<DailyPlanResponse.DailyPlanItemResponse> plans = Stream.concat(
                    basicPlanRepository.findDailyBasicPlans(userId, date).stream()
                            .map(DailyPlanResponse.DailyPlanItemResponse::fromBasic),
                    aiPlanRepository.findDailyAiPlans(userId, date).stream()
                            .map(DailyPlanResponse.DailyPlanItemResponse::fromAi)
                )
                .sorted(Comparator.comparing(DailyPlanResponse.DailyPlanItemResponse::scheduledTime))
                .toList();

        Map<Boolean, List<DailyPlanResponse.DailyPlanItemResponse>> plansByStatus = plans.stream()
                .collect(Collectors.partitioningBy(DailyPlanResponse.DailyPlanItemResponse::isDone));

        return DailyPlanResponse.of(plansByStatus.get(false), plansByStatus.get(true));
    }

    /**
     * 일정 상세 조회
     */
    public PlanDetailResponse getPlanDetail(Long userId, Long planId, PlanType planType) {
        return switch (planType) {
            case PlanType.BASIC -> getBasicPlanDetail(userId, planId);
            case PlanType.AI -> getAiPlanDetail(userId, planId);
            default -> throw BaseException.type(CommonErrorCode.INVALID_REQUEST);
        };
    }

    private PlanDetailResponse getBasicPlanDetail(Long userId, Long planId) {
        BasicPlan basicPlan = basicPlanRepository.findById(planId)
                .orElseThrow(() -> BaseException.type(BasicPlanErrorCode.BASIC_PLAN_NOT_FOUND));

        if (!userId.equals(basicPlan.getUser().getId())) {
            throw BaseException.type(BasicPlanErrorCode.BASIC_PLAN_FORBIDDEN);
        }
        return PlanDetailResponse.fromBasic(basicPlan);
    }

    private PlanDetailResponse getAiPlanDetail(Long userId, Long planId) {
        AiPlan aiPlan = aiPlanRepository.findById(planId)
                .orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND));

        if (!userId.equals(aiPlan.getPlan().getUser().getId())) {
            throw BaseException.type(AiPlanErrorCode.AI_PLAN_FORBIDDEN);
        }
        return PlanDetailResponse.fromAi(aiPlan);
    }

    public PlanDurationResponse getExpectedDuration(PlanDurationCommand command) {
        Long expectedDuration;

        if (command.planType() == PlanType.BASIC) {
            BasicPlan basicPlan = basicPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            expectedDuration = basicPlan.getExpectedDuration();
        } else if (command.planType() == PlanType.AI) {
            AiPlan aiPlan = aiPlanQueryService.getByPlanIdAndUserId(command.planId(), command.userId());
            expectedDuration = (long) aiPlan.getExpectedDuration();
        } else {
            throw BaseException.type(PlanErrorCode.PLAN_TYPE_NOT_FOUND);
        }

        return PlanDurationResponse.of(command.planType(), command.planId(), expectedDuration);
    }
}
