package com.miruni.backend.domain.plan.controller;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.request.AiPlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanDeleteResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanUpdateResponse;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.repository.AiPlanRespository;
import com.miruni.backend.domain.plan.service.AiPlanCommandService;
import com.miruni.backend.domain.plan.service.GeminiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/ai-plans")
@RequiredArgsConstructor
public class AiPlanController implements AiPlanApi {

        private final AiPlanCommandService aiPlanCommandService;
        private final AiPlanRespository aiPlanRespository;

        @PostMapping
        @Override
        public Mono<ResponseEntity<List<AiPlanCreateResponse>>> createAiPlan(
                @RequestParam Long userId,
                @RequestBody @Valid AiPlanCreateRequest request
                ){
                Plan savedPlan = aiPlanCommandService.createAndSavePlan(request, userId);

                return aiPlanCommandService.createAndSaveAiPlans(request, savedPlan)
                        .map(ResponseEntity::ok)
                        .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @PatchMapping("/{ai_plan_id}")
        @Override
        public AiPlanUpdateResponse updateAiPlan(
                @RequestParam Long userId,
                @PathVariable Long ai_plan_id,
                @RequestBody @Valid AiPlanUpdateRequest request
        ){
                return aiPlanCommandService.updateAiPlan(ai_plan_id, request, userId);
        }

        @DeleteMapping("/{ai_plan_id}")
        @Override
        public AiPlanDeleteResponse deleteAiPlan(
                @PathVariable Long ai_plan_id,
                @RequestParam Long user_id
        ){
                return aiPlanCommandService.deleteAiPlan(ai_plan_id, user_id);
        }



}
