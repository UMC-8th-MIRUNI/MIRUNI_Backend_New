package com.miruni.backend.domain.plan.entity;

import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ai_plan")
public class AiPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "sub_title", nullable = false, length = 50)
    private String subTitle;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "expected_duration", nullable = false)
    private int expectedDuration;

    @Column(name = "is_done", nullable = false)
    private boolean isDone = false;

    @Builder(access = AccessLevel.PRIVATE)
    public AiPlan(Plan plan, final String subTitle, final LocalDate scheduledDate, final LocalTime scheduledTime, final LocalTime endTime ,final int expectedDuration) {
        this.plan = plan;
        this.subTitle = subTitle;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.endTime = endTime;
        this.expectedDuration = expectedDuration;
    }

    public static AiPlan create(Plan plan, final String subTitle, final LocalDate scheduledDate, final LocalTime scheduledTime, final LocalTime endTime, final int expectedDuration) {
        return AiPlan.builder()
                .plan(plan)
                .subTitle(subTitle)
                .scheduledDate(scheduledDate)
                .scheduledTime(scheduledTime)
                .endTime(endTime)
                .expectedDuration(expectedDuration)
                .build();
    }

    public void updateDetails(String subTitle, LocalDate scheduledDate, LocalTime scheduledTime, LocalTime endTime, int expectedDuration) {
        this.subTitle = subTitle;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.endTime = endTime;
        this.expectedDuration = expectedDuration;
    }
    public void complete() { this.isDone = true; }

    public void rescheduleTime(LocalTime newScheduledTime) {this.scheduledTime = newScheduledTime;}
}
