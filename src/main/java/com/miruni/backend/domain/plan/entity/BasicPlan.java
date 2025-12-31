package com.miruni.backend.domain.plan.entity;

import com.miruni.backend.domain.plan.exception.BasicPlanErrorCode;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.common.BaseEntity;
import com.miruni.backend.global.exception.BaseException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

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

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "expected_duration", nullable = false)
    private Long expectedDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    private Priority priority;

    public void update(String title, String description, LocalDateTime startDateTime,
                       LocalDateTime endDateTime, Priority priority) {
        validateTimeRange(startDateTime, endDateTime);

        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.expectedDuration = Duration.between(startDateTime, endDateTime).toMinutes();
        this.priority = priority;
    }

    public static BasicPlan create(User user, String title, String description, LocalDateTime startDateTime,
                            LocalDateTime endDateTime, Priority priority) {
        long expectedDuration = Duration.between(startDateTime, endDateTime).toMinutes();
        return BasicPlan.builder()
                .user(user)
                .title(title)
                .description(description)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .expectedDuration(expectedDuration)
                .priority(priority)
                .build();
    }

    private static void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw BaseException.type(BasicPlanErrorCode.INVALID_TIME_RANGE);
        }
    }

    public void complete() { this.status = Status.DONE; }
    public void start() { this.status = Status.IN_PROGRESS; }
    public void pause() { this.status = Status.TODO; }
    public void rescheduleTime(LocalDateTime newScheduledTime) {
        this.startDateTime = newScheduledTime;
        this.endDateTime = newScheduledTime.plusMinutes(this.getExpectedDuration());
    }
}
