package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.repository.BasicPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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
}
