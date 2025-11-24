package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {

    Optional<AiPlan> findByIdAndPlanUserId(Long id, Long userId);
}
