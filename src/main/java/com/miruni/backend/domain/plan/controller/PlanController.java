package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.command.PlanFinishCommand;
import com.miruni.backend.domain.plan.dto.command.PlanPauseCommand;
import com.miruni.backend.domain.plan.dto.request.PlanFinishRequest;
import com.miruni.backend.domain.plan.dto.request.PlanPauseRequest;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.dto.response.PlanFinishResponse;
import com.miruni.backend.domain.plan.dto.response.PlanPauseResponse;
import com.miruni.backend.domain.plan.service.PlanCommandService;
import com.miruni.backend.domain.plan.service.PlanQueryService;
import com.miruni.backend.domain.plan.type.PlanType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController implements PlanApi {

    private final PlanQueryService planQueryService;
    private final PlanCommandService planCommandService;

    @Override
    @GetMapping("/duration")
    public PlanDurationResponse getExpectedDuration(@RequestParam Long userId,
                                                    @RequestParam PlanType planType,
                                                    @RequestParam Long id) {
        PlanDurationCommand command = PlanDurationCommand.of(userId, planType, id);
        return planQueryService.getExpectedDuration(command);
    }

    @Override
    @PostMapping("/finish")
    public PlanFinishResponse finishPlan(
            @RequestParam Long userId,
            @Valid @RequestBody PlanFinishRequest request
    ) {
        PlanFinishCommand command = PlanFinishCommand.of(
                request.planType(),
                request.id(),
                userId,
                request.expectedTime(),
                request.actualTime()
        );

        return planCommandService.finishPlan(command);
    }

    @Override
    @PatchMapping("/pause")
    public PlanPauseResponse pausePlan(
            @RequestParam Long userId,
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
