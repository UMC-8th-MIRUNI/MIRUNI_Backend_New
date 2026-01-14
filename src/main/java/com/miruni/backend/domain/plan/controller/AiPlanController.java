package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.command.*;
import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.request.AiPlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.request.AiPlansDeleteRequest;
import com.miruni.backend.domain.plan.dto.request.PlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.response.*;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.service.AiPlanCommandService;
import com.miruni.backend.domain.plan.service.AiPlanQueryService;
import com.miruni.backend.domain.plan.service.PlanCommandService;
import com.miruni.backend.domain.plan.service.PlanQueryService;
import com.miruni.backend.global.authroize.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/ai-plans")
@RequiredArgsConstructor
public class AiPlanController implements AiPlanApi {

        private final AiPlanCommandService aiPlanCommandService;
        private final AiPlanRepository aiPlanRepository;
        private final PlanQueryService planQueryService;
        private final AiPlanQueryService aiPlanQueryService;
        private final PlanCommandService planCommandService;

        @PostMapping
        @Override
        public List<AiPlanCreateResponse> createAiPlan(
                @AuthenticationPrincipal CustomUserDetails userDetails,
                @RequestBody @Valid AiPlanCreateRequest request
                ){
                Long userId = userDetails.getId();
                PlanCreateCommandDto planCreateCommand = PlanCreateCommandDto.from(userId, request);
                Plan savedPlan = aiPlanCommandService.savePlan(planCreateCommand);

                AiPlanCreateCommandDto aiPlanCreateCommand = AiPlanCreateCommandDto.from(request, savedPlan.getId());

                return aiPlanCommandService.saveAiPlans(aiPlanCreateCommand, userId);
        }

        @PatchMapping("/{ai-plan-id}")
        @Override
        public AiPlanUpdateResponse updateAiPlan(
                @AuthenticationPrincipal CustomUserDetails userDetails,
                @PathVariable("ai-plan-id") Long aiPlanId,
                @RequestBody @Valid AiPlanUpdateRequest request
        ){
                Long userId = userDetails.getId();
                AiPlanUpdateCommandDto aiPlanUpdateCommand = AiPlanUpdateCommandDto.from(userId, aiPlanId, request);
                return aiPlanCommandService.updateAiPlan(aiPlanId, aiPlanUpdateCommand);
        }

        @DeleteMapping("/{ai-plan-id}")
        @Override
        public AiPlanDeleteResponse deleteAiPlan(
                @PathVariable("ai-plan-id") Long aiPlanId,
                @AuthenticationPrincipal CustomUserDetails userDetails
        ){
                Long userId = userDetails.getId();
                return aiPlanCommandService.deleteAiPlan(aiPlanId, userId);
        }

        @GetMapping
        @Override
        public PlanReadResponse readPlan(
                @AuthenticationPrincipal CustomUserDetails userDetails
        ){
                Long userId = userDetails.getId();
                return planQueryService.findPlans(userId);
        }

        @GetMapping("/table/{plan-id}")
        @Override
        public AiPlanResponse readAiPlan(
                @PathVariable("plan-id") Long planId,
                @AuthenticationPrincipal CustomUserDetails userDetails
        ){
                Long userId = userDetails.getId();
                return aiPlanQueryService.findAiPlans(userId, planId);
        }

        @DeleteMapping("/table/{plan-id}")
        @Override
        public PlanDeleteAllResponse deletePlanTable(
                @PathVariable("plan-id") Long planId,
                @AuthenticationPrincipal CustomUserDetails userDetails
        ){
                Long userId = userDetails.getId();
                return planCommandService.deletePlanAll(userId, planId);
        }

        @PatchMapping("/table/{plan-id}")
        @Override
        public AiPlanResponse updatePlanTable(
                @PathVariable("plan-id") Long planId,
                @AuthenticationPrincipal CustomUserDetails userDetails,
                @RequestBody @Valid PlanUpdateRequest request
        ){
                Long userId = userDetails.getId();
                PlanUpdateCommandDto command = PlanUpdateCommandDto.from(userId, planId, request);
                return planCommandService.updatePlanTable(command);
        }

        @DeleteMapping("/table/items/{plan-id}")
        @Override
        public AiPlansDeleteResponse deleteAiPlanItems(
                @PathVariable("plan-id") Long planId,
                @AuthenticationPrincipal CustomUserDetails userDetails,
                @RequestBody @Valid AiPlansDeleteRequest request
        ){
                Long userId = userDetails.getId();
                AiPlansDeleteCommandDto command = AiPlansDeleteCommandDto.from(planId, request, userId);
                return aiPlanCommandService.deleteAiPlanItems(command);
        }

}
