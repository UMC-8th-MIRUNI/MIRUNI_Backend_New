package com.miruni.backend.domain.plan.entity;

import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "basic_plan")
public class BasicPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "basic_plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time", nullable = false, columnDefinition = "TIME")
    private LocalTime scheduledTime;

    @Column(name = "expected_duration", nullable = false)
    private Long expectedDuration;

    @Column(name = "is_done", nullable = false)
    @Builder.Default
    private boolean isDone = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    private Priority priority;

}
