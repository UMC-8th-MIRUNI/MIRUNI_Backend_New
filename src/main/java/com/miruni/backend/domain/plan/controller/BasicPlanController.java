package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.BasicPlanCreateRequest;
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
    public BasicPlanResponse createBasicPlan(
            @RequestParam Long userId,
            @Valid @RequestBody BasicPlanCreateRequest request
    ) {
        return basicPlanCommandService.createBasicPlan(userId, request);
    }
}