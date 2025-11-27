package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.response.DailyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.PlanDetailResponse;
import com.miruni.backend.domain.plan.service.PlanQueryService;
import com.miruni.backend.domain.plan.type.PlanType;
import com.miruni.backend.global.authroize.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController implements PlanApi{

    private final PlanQueryService planQueryService;

    @GetMapping("/monthly")
    public List<MonthlyPlanResponse> getMonthlyPlans(@LoginUser Long userId, @RequestParam int year, @RequestParam int month) {
        return planQueryService.getMonthlyPlan(userId, year, month);
    }

    @GetMapping("/daily")
    public DailyPlanResponse getDailyPlans(@LoginUser Long userId, @RequestParam int year, @RequestParam int month, @RequestParam int day) {
        return planQueryService.getDailyPlan(userId, year, month, day);
    }

    @GetMapping("/{planId}")
    public PlanDetailResponse getPlanDetail(@LoginUser Long userId, @PathVariable Long planId, @RequestParam PlanType planType) {
        return planQueryService.getPlanDetail(userId, planId, planType);
    }
}
