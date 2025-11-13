package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiPlanRespository extends JpaRepository<AiPlan, Long> {
}
