package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AiPlanRespository extends JpaRepository<AiPlan, Long> {

    @Query("select a.plan from AiPlan a where a.id=:aiPlanId")
    Plan findPlanByAiPlanId(@Param("aiPlanId") Long aiPlanId);
}
