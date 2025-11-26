package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.response.DailyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PlanQueryService {

    private final AiPlanRepository aiPlanRepository;
    private final BasicPlanRepository basicPlanRepository;

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
                .map(e -> new MonthlyPlanResponse(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(MonthlyPlanResponse::date))
                .toList();
    }

    public DailyPlanResponse getDailyPlan(Long userId, int year, int month, int day) {

        LocalDate date = LocalDate.of(year, month, day);

        List<DailyPlanResponse.DailyPlanItemResponse> allPlans = Stream.concat(
                basicPlanRepository.findDailyBasicPlans(userId, date).stream()
                        .map(DailyPlanResponse.DailyPlanItemResponse::fromBasic),
                aiPlanRepository.findDailyAiPlans(userId, date).stream()
                        .map(DailyPlanResponse.DailyPlanItemResponse::fromAi)
        ).toList();

        Map<Boolean, List<DailyPlanResponse.DailyPlanItemResponse>> plansByStatus =
                allPlans.stream()
                        .collect(Collectors.partitioningBy(DailyPlanResponse.DailyPlanItemResponse::isDone));
        // TODO: scheduledTime으로 정렬 추가할 것

        return new DailyPlanResponse(
                plansByStatus.get(false),
                plansByStatus.get(true)
        );
    }
}
