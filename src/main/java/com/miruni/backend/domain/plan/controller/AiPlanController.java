package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.request.AiPlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanDeleteResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanUpdateResponse;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.service.AiPlanCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/ai-plans")
@RequiredArgsConstructor
public class AiPlanController implements AiPlanApi {

        private final AiPlanCommandService aiPlanCommandService;
        private final AiPlanRepository aiPlanRepository;

        @PostMapping
        @Override
        public Mono<List<AiPlanCreateResponse>> createAiPlan(
                @RequestParam Long userId,
                @RequestBody @Valid AiPlanCreateRequest request
                ){
                Plan savedPlan = aiPlanCommandService.savePlan(request, userId);

                return aiPlanCommandService.saveAiPlans(request, savedPlan);
        }

        @PatchMapping("/{ai-plan-id}")
        @Override
        public AiPlanUpdateResponse updateAiPlan(
                @RequestParam Long userId,
                @PathVariable("ai-plan-id") Long aiPlanId,
                @RequestBody @Valid AiPlanUpdateRequest request
        ){
                return aiPlanCommandService.updateAiPlan(aiPlanId, request, userId);
        }

        @DeleteMapping("/{ai-plan-id}")
        @Override
        public AiPlanDeleteResponse deleteAiPlan(
                @PathVariable("ai-plan-id") Long aiPlanId,
                @RequestParam Long user_id
        ){
                return aiPlanCommandService.deleteAiPlan(aiPlanId, user_id);
        }



}
