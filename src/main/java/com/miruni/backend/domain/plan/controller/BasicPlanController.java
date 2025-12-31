package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.command.BasicPlanCreateCommand;
import com.miruni.backend.domain.plan.dto.command.BasicPlanUpdateCommand;
import com.miruni.backend.domain.plan.dto.request.BasicPlanSaveRequest;
import com.miruni.backend.domain.plan.dto.response.BasicPlanResponse;
import com.miruni.backend.domain.plan.service.BasicPlanCommandService;
import com.miruni.backend.global.authroize.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class BasicPlanController implements BasicPlanApi{

    private final BasicPlanCommandService basicPlanCommandService;

    @Override
    @PostMapping
    public List<BasicPlanResponse> createBasicPlan(@LoginUser Long userId,
                                                   @Valid @RequestBody BasicPlanSaveRequest request) {

        return basicPlanCommandService.createBasicPlan(BasicPlanCreateCommand.of(userId, request));
    }

    @Override
    @PatchMapping("/{basicPlanId}")
    public BasicPlanResponse updateBasicPlan(@LoginUser Long userId,
                                             @PathVariable Long basicPlanId,
                                             @Valid @RequestBody BasicPlanSaveRequest request) {
        return basicPlanCommandService.updateBasicPlan(BasicPlanUpdateCommand.of(userId, basicPlanId, request));
    }

    @DeleteMapping("/{basicPlanId}")
    public Long deleteBasicPlan(@LoginUser Long userId,
                                @PathVariable Long basicPlanId) {
        return basicPlanCommandService.deleteBasicPlan(userId, basicPlanId);
    }
}