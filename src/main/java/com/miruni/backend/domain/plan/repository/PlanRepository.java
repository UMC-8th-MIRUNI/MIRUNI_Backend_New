package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.dto.response.AiPlanResponse;
import com.miruni.backend.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findAllByUserId(Long userId);
}
