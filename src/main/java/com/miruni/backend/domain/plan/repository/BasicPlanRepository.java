package com.miruni.backend.domain.plan.repository;

import com.miruni.backend.domain.plan.entity.BasicPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BasicPlanRepository extends JpaRepository<BasicPlan, Long> {
    Optional<BasicPlan> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndScheduledTime(Long userId, LocalTime scheduledTime);

    @Query("""
        SELECT COUNT(b) > 0
        FROM BasicPlan b
        WHERE b.user.id = :userId
            AND b.scheduledDate = :date
            AND (b.scheduledTime < :reqEndTime AND b.endTime > :reqStartTime)
    """)
    boolean existsOverlap(
            @Param("userId") Long userId,
            @Param("date") LocalDate date,
            @Param("reqStartTime") LocalTime reqStartTime,
            @Param("reqEndTime") LocalTime reqEndTime
    );

    @Query("""
        SELECT COUNT(b) > 0
        FROM BasicPlan b
        WHERE b.user.id = :userId
            AND b.id != :excludeId
            AND b.scheduledDate = :date
            AND (b.scheduledTime < :reqEndTime AND b.endTime > :reqStartTime)
    """)
    boolean existsOverlapWithinUpdate(
            @Param("userId") Long userId,
            @Param("excludeId") Long excludeId,
            @Param("date") LocalDate date,
            @Param("reqStartTime") LocalTime reqStartTime,
            @Param("reqEndTime") LocalTime reqEndTime
    );

}
