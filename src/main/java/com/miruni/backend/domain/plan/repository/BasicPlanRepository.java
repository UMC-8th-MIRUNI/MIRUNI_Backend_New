package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BasicPlanRepository extends JpaRepository<BasicPlan, Long> {
    Optional<BasicPlan> findByIdAndUserId(Long id, Long userId);

    @Query("""
        SELECT new com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse(
             b.scheduledDate,
             COUNT(b)
         )
        FROM BasicPlan b
        WHERE b.user.id = :userId
          AND b.isDone = false
          AND b.scheduledDate BETWEEN :startDate AND :endDate
        GROUP BY b.scheduledDate
        ORDER BY b.scheduledDate
    """)
    List<MonthlyPlanResponse> countUnfinishedBasicPlansByDate(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
        SELECT b
        FROM BasicPlan b
        WHERE b.user.id = :userId
          AND b.scheduledDate = :date
        ORDER BY b.scheduledTime
    """)
    List<BasicPlan> findDailyBasicPlans(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
}
