package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.entity.BasicPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicPlanRepository extends JpaRepository<BasicPlan, Long> {
}
