package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.command.PlanDurationCommand;
import com.miruni.backend.domain.plan.dto.response.PlanDurationResponse;
import com.miruni.backend.domain.plan.service.PlanQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController implements PlanApi {

    private final PlanQueryService planQueryService;

    @Override
    @GetMapping("/duration")
    public PlanDurationResponse getExpectedDuration(@RequestParam Long userId,
                                                    @RequestParam String planType,
                                                    @RequestParam Long id) {
        PlanDurationCommand command = PlanDurationCommand.of(userId, planType, id);
        return planQueryService.getExpectedDuration(command);
    }
}