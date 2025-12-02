package com.miruni.backend.domain.plan.service;

import com.miruni.backend.domain.plan.dto.command.AiPlanCreateCommandDto;
import com.miruni.backend.domain.plan.dto.command.AiPlanUpdateCommandDto;
import com.miruni.backend.domain.plan.dto.command.AiPlansDeleteCommandDto;
import com.miruni.backend.domain.plan.dto.command.PlanCreateCommandDto;
import com.miruni.backend.domain.plan.dto.response.AiPlanCreateResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanDeleteResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlanUpdateResponse;
import com.miruni.backend.domain.plan.dto.response.AiPlansDeleteResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import com.miruni.backend.domain.plan.exception.AiPlanErrorCode;
import com.miruni.backend.domain.plan.repository.AiPlanRepository;
import com.miruni.backend.domain.plan.repository.PlanRepository;
import com.miruni.backend.domain.plan.validator.ScheduleValidator;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AiPlanCommandService {

    private final AiPlanRepository aiPlanRepository;
    private final PlanRepository planRepository;
    private final GeminiService geminiService;
    private final UserQueryService userQueryService;
    private final PlanQueryService planQueryService;
    private final ScheduleValidator scheduleValidator;


    @Transactional
    public Plan savePlan(PlanCreateCommandDto command) {
        User user = userQueryService.getUserById(command.userId());

        Plan newPlan = Plan.create(
                user,
                command.title(),
                command.deadline().atStartOfDay(),
                command.taskRange(),
                command.priority()
        );
        return planRepository.save(newPlan);
    }

    public List<AiPlanCreateResponse> saveAiPlans(AiPlanCreateCommandDto command, Long userId) {
        User user = userQueryService.getUserById(userId);
        user.deductAiChance();

        Plan plan = planQueryService.findById(command.planId());
        List<AiPlanCreateResponse> dtoList = this.geminiService.getAiPlanFromApi(command).block();

        if (dtoList != null && !dtoList.isEmpty()) {
            for (AiPlanCreateResponse dto : dtoList) {
                scheduleValidator.validateConflict(
                        userId,
                        dto.scheduledDate(),
                        dto.startTime(),
                        dto.endTime()
                );
            }

            List<AiPlan> entityToSave = dtoList.stream()
                    .map(dto -> AiPlan.create(
                            plan,
                            dto.subTitle(),
                            dto.scheduledDate(),
                            dto.startTime(),
                            dto.endTime(),
                            dto.expectedDuration()
                    ))
                    .toList();

            List<AiPlan> savedEntity = aiPlanRepository.saveAll(entityToSave);

            return savedEntity.stream()
                    .map(entity -> AiPlanCreateResponse.fromEntity(entity, plan))
                    .toList();

        }
        return List.of();
    }

    public AiPlanUpdateResponse updateAiPlan(Long aiPlanId, AiPlanUpdateCommandDto command) {
        User user = userQueryService.getUserById(command.userId());
        AiPlan aiPlan = aiPlanRepository.findById(aiPlanId).orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND));
        Plan plan = aiPlan.getPlan();

        if(!plan.getUser().getId().equals(user.getId())) {
            throw BaseException.type(CommonErrorCode.FORBIDDEN);
        }

        scheduleValidator.validateConflict(command.userId(), aiPlanId, command.scheduledDate(), command.startTime(), command.endTime());
        plan.updateTitle(command.title());
        aiPlan.updateDetails(
                command.subTitle(),
                command.scheduledDate(),
                command.startTime(),
                command.endTime(),
                aiPlan.getExpectedDuration()

        );
        return AiPlanUpdateResponse.fromEntity(aiPlan, plan);
    }

    public AiPlanDeleteResponse deleteAiPlan(Long aiPlan_id, Long userId ) {
        AiPlan aiPlan = aiPlanRepository.findById(aiPlan_id).orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND));
        User user = userQueryService.getUserById(userId);

        if (!aiPlan.getPlan().getUser().getId().equals(user.getId())) {
            throw BaseException.type(CommonErrorCode.FORBIDDEN);
        }
        aiPlanRepository.delete(aiPlan);

        return new AiPlanDeleteResponse(true);
    }

    public AiPlansDeleteResponse deleteAiPlanItems(AiPlansDeleteCommandDto command) {
        List<Long> targetIds = command.aiPlansId();

        List<AiPlan> targetAiPlans = aiPlanRepository.findAllById(targetIds);

        if (targetAiPlans.size() != targetIds.size()){
            throw BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND);
        }

        for (AiPlan aiPlan : targetAiPlans) {
            if (!aiPlan.getPlan().getId().equals(command.userId())) {
                throw BaseException.type(AiPlanErrorCode.PLAN_NOT_MATCH);
            }
            if (!aiPlan.getPlan().getUser().getId().equals(command.userId())) {
                throw BaseException.type(CommonErrorCode.FORBIDDEN);
            }
        }
        aiPlanRepository.deleteAllInBatch(targetAiPlans);

        return new AiPlansDeleteResponse(true);
    }
}
