package com.miruni.backend.domain.plan.entity;

import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name="start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "expected_duration", nullable = false)
    private int expectedDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.TODO;

    @Builder(access = AccessLevel.PRIVATE)
    public AiPlan(Plan plan, final String subTitle, final LocalDateTime startDateTime, final LocalDateTime endDateTime, final int expectedDuration, final Status status) {
        this.plan = plan;
        this.subTitle = subTitle;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.expectedDuration = expectedDuration;
        this.status = status;
    }

    public static AiPlan create(Plan plan, final String subTitle, final LocalDateTime startDateTime, final LocalDateTime endDateTime, final int expectedDuration, final Status status) {
        return AiPlan.builder()
                .plan(plan)
                .subTitle(subTitle)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .expectedDuration(expectedDuration)
                .status(status)
                .build();
    }

    public void updateDetails(String subTitle, LocalDateTime startDateTime, LocalDateTime endDateTime,  int expectedDuration, Status status) {
        this.subTitle = subTitle;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.expectedDuration = expectedDuration;
        this.status = status;
    }
    public void complete() { this.status = Status.DONE; }
    public void start() { this.status = Status.IN_PROGRESS; }
    public void pause() { this.status = Status.TODO; }

    public void rescheduleTime(LocalDateTime newScheduledTime) {
        this.startDateTime = newScheduledTime;
        this.endDateTime = newScheduledTime.plusMinutes(this.getExpectedDuration());
    }
}
