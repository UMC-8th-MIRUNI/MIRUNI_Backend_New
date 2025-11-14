package com.miruni.backend.domain.plan.entity;

import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ai_plan")
public class AiPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Plan plan;

    @Column(name = "sub_title", nullable = false, length = 50)
    private String subTitle;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;

    @Column(name = "expected_duration", nullable = false)
    private int expectedDuration;

    @Column(name = "is_done", nullable = false)
    @Builder.Default
    private boolean isDone = false;

    public void updateDetails(String subTitle, LocalDate scheduledDate, LocalTime scheduledTime) {
        this.subTitle = subTitle;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
    }

}
