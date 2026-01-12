package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.request.AiPlanUpdateRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanDeleteResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanUpdateResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.repository.PlanRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AiPlanCommandService {

    private final AiPlanRepository aiPlanRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    @Transactional
    public Plan savePlan(AiPlanCreateRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        Plan savedPlan = planRepository.save(request.toEntity(user));
        return savedPlan;
    }

    public Mono<List<AiPlanCreateResponse>> saveAiPlans(AiPlanCreateRequest request, Plan plan) {
        Mono<List<AiPlanCreateResponse>> mono = this.geminiService.getAiPlanFromApi(request, plan.getId());

        return mono.flatMap(tempDtoList -> {
            List<AiPlan> entityToSave = tempDtoList.stream()
                    .map(dto -> dto.toEntity(plan))
                    .toList();
            List<AiPlan> savedEntity = aiPlanRepository.saveAll(entityToSave);

            List<AiPlanCreateResponse> finalDtoList = savedEntity.stream()
                    .map(entity -> AiPlanCreateResponse.fromEntity(entity, plan))
                    .toList();

            return Mono.just(finalDtoList);

        });

    }

    public AiPlanUpdateResponse updateAiPlan(Long aiPlan_id, AiPlanUpdateRequest request, Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
        AiPlan aiPlan = aiPlanRepository.findById(aiPlan_id).orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND));
        Plan plan = aiPlan.getPlan();

        if(!plan.getUser().getId().equals(user.getId())) {
            throw BaseException.type(AiPlanErrorCode.AI_PLAN_FORBIDDEN);
        }

        plan.updateTitle(request.title());
        aiPlan.updateDetails(
                request.subTitle(),
                request.scheduledDate(),
                request.startTime()
        );
        return AiPlanUpdateResponse.fromEntity(aiPlan, plan);
    }

    public AiPlanDeleteResponse deleteAiPlan(Long aiPlan_id, Long user_id ) {
        AiPlan aiPlan = aiPlanRepository.findById(aiPlan_id).orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND));
        User user = userRepository.findById(user_id).orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        if (!aiPlan.getPlan().getUser().getId().equals(user.getId())) {
            throw BaseException.type(AiPlanErrorCode.AI_PLAN_FORBIDDEN);
        }
        aiPlanRepository.delete(aiPlan);

        return new AiPlanDeleteResponse(true);
    }
}
