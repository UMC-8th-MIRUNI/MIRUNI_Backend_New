package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.response.*;
import com.miruni.backend.domain.plan.dto.response.PlanPreviewDto;
import com.miruni.backend.domain.plan.dto.response.PlanReadResponse;
import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.dto.response.DailyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.PlanDetailResponse;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Status;
import com.miruni.backend.domain.plan.exception.PlanErrorCode;
import com.miruni.backend.domain.plan.repository.PlanRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.plan.exception.PlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import com.miruni.backend.domain.plan.type.PlanType;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanQueryService {
    private final PlanRepository planRepository;
    private final UserQueryService userQueryService;
    private final AiPlanRepository aiPlanRepository;
    private final BasicPlanRepository basicPlanRepository;
    private final BasicPlanQueryService basicPlanQueryService;

    public Plan findById(Long planId) {
        return planRepository.findById(planId).orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));
    }

    public PlanReadResponse findPlans(Long userId) {
        User user = userQueryService.getUserById(userId);
        int remainingCnt = user.getRemainChance();

        List<Plan> plans = planRepository.findAllByUserId(userId);
        List<PlanPreviewDto> planDtos = plans.stream()
                .map(plan -> {
                    List<AiPlan> aiPlans = plan.getAiPlans();

                    int totalCnt = aiPlans.size();
                    int doneCnt = (int) aiPlans.stream()
                            .filter(aiPlan -> aiPlan.getStatus() == Status.DONE)
                            .count();
                    int progressRate = (totalCnt == 0) ? 0 : (int) ((double) doneCnt / totalCnt * 100);

                    boolean isDone = (totalCnt > 0 && doneCnt == totalCnt);

                    return  PlanPreviewDto.of(
                            plan.getId(),
                            plan.getTitle(),
                            doneCnt,
                            totalCnt,
                            progressRate,
                            isDone
                    );
                })
                .toList();

        return PlanReadResponse.of(remainingCnt, planDtos);
    }

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
                        DailyPlanResponse.DailyPlanItemResponse::startTime
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

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<MonthlyPlanResponse> basicPlans = basicPlanRepository.countUnfinishedBasicPlansByDate(userId, startDateTime, endDateTime);
        List<MonthlyPlanResponse> aiPlans = aiPlanRepository.countUnfinishedAiPlansByDate(userId, startDateTime, endDateTime);

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
                .sorted(Comparator.comparing(DailyPlanResponse.DailyPlanItemResponse::startTime))
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
}
