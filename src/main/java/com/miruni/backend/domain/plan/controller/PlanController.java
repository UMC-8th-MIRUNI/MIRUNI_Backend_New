package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.command.PlanFinishCommand;
import com.miruni.backend.domain.plan.dto.command.PlanPauseCommand;
import com.miruni.backend.domain.plan.dto.request.PlanStartRequest;
import com.miruni.backend.domain.plan.dto.request.PlanFinishRequest;
import com.miruni.backend.domain.plan.dto.request.PlanPauseRequest;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.dto.response.PlanFinishResponse;
import com.miruni.backend.domain.plan.dto.response.PlanPauseResponse;
import com.miruni.backend.domain.plan.dto.response.PlanStartResponse;
import com.miruni.backend.domain.plan.service.PlanCommandService;
import com.miruni.backend.domain.plan.service.PlanDurationQueryService;
import com.miruni.backend.domain.plan.type.PlanType;
import com.miruni.backend.global.authroize.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController implements PlanApi {

    private final PlanDurationQueryService planDurationQueryService;
    private final PlanCommandService planCommandService;

    @Override
    @GetMapping("/{planId}/expected-duration")
    public PlanDurationResponse getExpectedDuration(@LoginUser Long userId,
                                                    @RequestParam PlanType planType,
                                                    @PathVariable Long planId) {
        return planDurationQueryService.getExpectedDuration(PlanDurationCommand.of(userId, planType, planId));
    }

    @Override
    @PostMapping("/{planId}/start")
    public PlanStartResponse startPlan(@LoginUser Long userId,
                                       @RequestParam PlanType planType,
                                       @PathVariable Long planId,
                                       @RequestParam String durationStr ) {
        return planCommandService.startPlan(PlanStartRequest.of(userId, planType, planId, durationStr));
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
