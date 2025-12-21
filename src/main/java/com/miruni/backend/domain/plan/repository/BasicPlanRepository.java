package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.entity.BasicPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface BasicPlanRepository extends JpaRepository<BasicPlan, Long> {
    Optional<BasicPlan> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndScheduledTime(Long userId, LocalTime scheduledTime);
}
