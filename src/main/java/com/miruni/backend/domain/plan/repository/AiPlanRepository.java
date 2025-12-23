package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {

    @Query("""
        SELECT new com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse(
            a.scheduledDate,
            COUNT(a)
        )
        FROM AiPlan a
        WHERE a.plan.user.id = :userId
          AND a.isDone = false
          AND a.scheduledDate BETWEEN :startDate AND :endDate
        GROUP BY a.scheduledDate
        ORDER BY a.scheduledDate
    """)
    List<MonthlyPlanResponse> countUnfinishedAiPlansByDate(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT a
        FROM AiPlan a
        WHERE a.plan.user.id = :userId
          AND a.scheduledDate = :date
        ORDER BY a.scheduledTime
    """)
    List<AiPlan> findDailyAiPlans(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
    Optional<AiPlan> findByIdAndPlanUserId(Long id, Long userId);
    List<AiPlan> findByPlanId(Long planId);
    boolean existsByPlanUserIdAndScheduledTime(Long userId, LocalTime scheduledTime);
}
