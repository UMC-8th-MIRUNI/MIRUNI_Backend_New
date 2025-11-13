package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.request.AiPlanCreateRequest;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.repository.AiPlanRespository;
import com.miruni.backend.domain.plan.repository.PlanRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiPlanCommandService {

    private final AiPlanRespository aiPlanRespository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    @Transactional
    public Plan createAndSavePlan(AiPlanCreateRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        Plan savedPlan = planRepository.save(request.toEntity(user));
        return savedPlan;
    }

    public Mono<List<AiPlanCreateResponse>> createAndSaveAiPlans(AiPlanCreateRequest request, Plan plan) {
        Mono<List<AiPlanCreateResponse>> mono = this.geminiService.getAiPlanFromApi(request, plan.getId());

        return mono.flatMap(tempDtoList -> {
            List<AiPlan> entityToSave = tempDtoList.stream()
                    .map(dto -> dto.toEntity(plan))
                    .toList();
            List<AiPlan> savedEntity = aiPlanRespository.saveAll(entityToSave);

            List<AiPlanCreateResponse> finalDtoList = savedEntity.stream()
                    .map(entity -> AiPlanCreateResponse.fromEntity(entity,plan))
                    .toList();

            return Mono.just(finalDtoList);

        });

    }
}
