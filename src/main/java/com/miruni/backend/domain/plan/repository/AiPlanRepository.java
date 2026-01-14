package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse;
import com.miruni.backend.domain.plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {

    @Query("""
        SELECT new com.miruni.backend.domain.plan.dto.response.MonthlyPlanResponse(
            CAST(a.startDateTime AS LocalDate),
            COUNT(a)
        )
        FROM AiPlan a
        WHERE a.plan.user.id = :userId
          AND a.status != com.miruni.backend.domain.plan.entity.Status.DONE
          AND a.startDateTime >= :startDateTime
          AND a.startDateTime < :endDateTime
        GROUP BY FUNCTION('DATE', a.startDateTime)
        ORDER BY FUNCTION('DATE', a.startDateTime)
    """)
    List<MonthlyPlanResponse> countUnfinishedAiPlansByDate(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        SELECT a
        FROM AiPlan a
        WHERE a.plan.user.id = :userId
          AND FUNCTION('DATE', a.startDateTime) = :date
        ORDER BY a.startDateTime
    """)
    List<AiPlan> findDailyAiPlans(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    Optional<AiPlan> findByIdAndPlanUserId(Long id, Long userId);

    List<AiPlan> findByPlanId(Long planId);
//    boolean existsByPlanUserIdAndScheduledTime(Long userId, LocalTime scheduledTime);
    boolean existsByPlanUserIdAndStartDateTime(Long userId, LocalDateTime startDateTime);

    @Query("""
        SELECT COUNT(a) > 0
        FROM AiPlan a
        JOIN a.plan p
        WHERE p.user.id = :userId
            AND (a.startDateTime < :reqEndDateTime AND a.endDateTime > :reqStartDateTime)
    """)
    boolean existsOverlap(
            @Param("userId") Long userId,
            @Param("reqStartDateTime") LocalDateTime reqStartDateTime,
            @Param("reqEndDateTime") LocalDateTime reqEndDateTime
    );

    @Query("""
        SELECT COUNT(a) > 0 
        FROM AiPlan a
        JOIN a.plan p
        WHERE p.user.id = :userId
            AND a.id != :excludeId
            AND (a.startDateTime < :reqEndDateTime AND a.endDateTime > :reqStartDateTime)
    """)
    boolean existsOverlapWithinUpdate(
            @Param("userId") Long userId,
            @Param("excludeId") Long excludeId,
            @Param("reqStartDateTime") LocalDateTime reqStartDateTime,
            @Param("reqEndDateTime") LocalDateTime reqEndDateTime
    );
}
