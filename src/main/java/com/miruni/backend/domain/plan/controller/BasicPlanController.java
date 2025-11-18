package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.command.BasicPlanCreateCommandDto;
import com.miruni.backend.domain.plan.dto.command.BasicPlanUpdateCommandDto;
import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;
import com.miruni.backend.domain.plan.dto.response.BasicPlanResponse;
import com.miruni.backend.domain.plan.service.BasicPlanCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class BasicPlanController implements BasicPlanApi{

    private final BasicPlanCommandService basicPlanCommandService;

    @Override
    @PostMapping
    public BasicPlanResponse createBasicPlan(@RequestParam Long userId,
                                             @Valid @RequestBody BasicPlanSaveRequest request) {
        BasicPlanCreateCommandDto command = BasicPlanCreateCommandDto.of(userId, request);

        return basicPlanCommandService.createBasicPlan(command);
    }

    @Override
    @PatchMapping("/{basicPlanId}")
    public BasicPlanResponse updateBasicPlan(@RequestParam Long userId,
                                             @PathVariable Long basicPlanId,
                                             @Valid @RequestBody BasicPlanSaveRequest request) {
        return basicPlanCommandService.updateBasicPlan(BasicPlanUpdateCommandDto.of(userId, basicPlanId, request));
    }

    @DeleteMapping("/{basicPlanId}")
    public Long deleteBasicPlan(@RequestParam Long userId,
                                @PathVariable Long basicPlanId) {
        return basicPlanCommandService.deleteBasicPlan(userId, basicPlanId);
    }
}