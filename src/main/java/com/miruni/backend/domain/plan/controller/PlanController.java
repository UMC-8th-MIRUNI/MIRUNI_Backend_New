package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.command.PlanFinishCommand;
import com.miruni.backend.domain.plan.dto.command.PlanPauseCommand;
import com.miruni.backend.domain.plan.dto.request.PlanFinishRequest;
import com.miruni.backend.domain.plan.dto.request.PlanPauseRequest;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.dto.response.PlanFinishResponse;
import com.miruni.backend.domain.plan.dto.response.PlanPauseResponse;
import com.miruni.backend.domain.plan.dto.response.DailyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.dto.response.PlanDetailResponse;
import com.miruni.backend.domain.plan.service.PlanCommandService;
import com.miruni.backend.domain.plan.service.PlanQueryService;
import com.miruni.backend.domain.plan.type.PlanType;
import com.miruni.backend.global.authroize.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController implements PlanApi{

    private final PlanCommandService planCommandService;
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

    @Override
    @GetMapping("/start")
    public PlanDurationResponse getExpectedDuration(@LoginUser Long userId,
                                                    @RequestParam PlanType planType,
                                                    @RequestParam Long id) {
        return planQueryService.getExpectedDuration(PlanDurationCommand.of(userId, planType, id));
    }

    @Override
    @PostMapping("/finish/{planType}/{id}")
    public PlanFinishResponse finishPlan(
            @LoginUser Long userId,
            @PathVariable PlanType planType,
            @PathVariable Long id,
            @Valid @RequestBody PlanFinishRequest request
    ) {
        PlanFinishCommand command = PlanFinishCommand.of(
                planType,
                id,
                userId,
                request.expectedTime(),
                request.actualTime()
        );

        return planCommandService.finishPlan(command);
    }

    @Override
    @PatchMapping("/pause")
    public PlanPauseResponse pausePlan(
            @LoginUser Long userId,
            @Valid @RequestBody PlanPauseRequest request
    ) {
        PlanPauseCommand command = PlanPauseCommand.of(
                request.planType(),
                request.planId(),
                userId,
                request.resumeTime()
        );
        return planCommandService.pausePlan(command);
    }
}
