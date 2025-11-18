package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
