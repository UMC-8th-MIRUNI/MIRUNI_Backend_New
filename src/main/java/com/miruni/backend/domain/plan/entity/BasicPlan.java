package com.miruni.backend.domain.plan.entity;

import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.common.BaseEntity;
import com.miruni.backend.global.exception.BaseException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
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

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "expected_duration", nullable = false)
    private Long expectedDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    private Priority priority;

    public void update(String title, String description, LocalDate scheduledDate,
                       LocalTime startTime, LocalTime endTime, Priority priority) {
        validateTimeRange(startTime, endTime);

        this.title = title;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = startTime;
        this.endTime = endTime;
        this.expectedDuration = Duration.between(startTime, endTime).toMinutes();
        this.priority = priority;
    }

    public static BasicPlan create(User user, String title, String description, LocalDate scheduledDate,
                            LocalTime startTime, LocalTime endTime, Priority priority) {
        validateTimeRange(startTime, endTime);
        long expectedDuration = Duration.between(startTime, endTime).toMinutes();
        return BasicPlan.builder()
                .user(user)
                .title(title)
                .description(description)
                .scheduledDate(scheduledDate)
                .scheduledTime(startTime)
                .endTime(endTime)
                .expectedDuration(expectedDuration)
                .priority(priority)
                .build();
    }

    private static void validateTimeRange(LocalTime start, LocalTime end) {
        if (start.isAfter(end)) {
            throw BaseException.type(BasicPlanErrorCode.INVALID_TIME_RANGE);
        }
    }

    public void complete() { this.status = Status.DONE; }
    public void start() { this.status = Status.IN_PROGRESS; }
    public void pause() { this.status = Status.TODO; }
    public void rescheduleTime(LocalTime newScheduledTime) {
        this.scheduledTime = newScheduledTime;
        this.endTime = newScheduledTime.plusMinutes(this.getExpectedDuration());
    }
}
