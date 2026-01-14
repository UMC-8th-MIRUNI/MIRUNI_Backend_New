package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface BasicPlanRepository extends JpaRepository<BasicPlan, Long> {
    Optional<BasicPlan> findByIdAndUserId(Long id, Long userId);

    @Query("""
        SELECT new com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse(
            CAST(b.startDateTime AS LocalDate),
            COUNT(b)
        )
        FROM BasicPlan b
        WHERE b.user.id = :userId
          AND b.status != com.miruni.backend.domain.plan.entity.Status.DONE
          AND b.startDateTime >= :startDateTime
          AND b.startDateTime < :endDateTime
        GROUP BY CAST(b.startDateTime AS LocalDate)
        ORDER BY CAST(b.startDateTime AS LocalDate)
    """)
    List<MonthlyPlanResponse> countUnfinishedBasicPlansByDate(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        SELECT b
        FROM BasicPlan b
        WHERE b.user.id = :userId
          AND FUNCTION('DATE', b.startDateTime) = :date
        ORDER BY b.startDateTime
    """)
    List<BasicPlan> findDailyBasicPlans(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    //boolean existsByUserIdAndScheduledTime(Long userId, LocalTime scheduledTime);

    //boolean existsByUserIdAndScheduledStartTime(Long userId, LocalTime scheduledTime);

    @Query("""
        SELECT COUNT(b) > 0
        FROM BasicPlan b
        WHERE b.user.id = :userId
              AND b.startDateTime < :reqEndDateTime
              AND b.endDateTime > :reqStartDateTime
    """)
    boolean existsOverlap(
            @Param("userId") Long userId,
            @Param("reqStartDateTime") LocalDateTime reqStartDateTime,
            @Param("reqEndDateTime") LocalDateTime reqEndDateTime
    );

    @Query("""
        SELECT COUNT(b) > 0
        FROM BasicPlan b
        WHERE b.user.id = :userId
              AND b.id != :excludeId
              AND b.startDateTime < :reqEndDateTime
              AND b.endDateTime > :reqStartDateTime
    """)
    boolean existsOverlapWithinUpdate(
            @Param("userId") Long userId,
            @Param("excludeId") Long excludeId,
            @Param("reqStartDateTime") LocalDateTime reqStartDateTime,
            @Param("reqEndDateTime") LocalDateTime reqEndDateTime
    );
}
