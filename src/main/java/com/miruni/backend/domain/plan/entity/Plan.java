package com.miruni.backend.domain.plan.entity;

import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plan")
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "is_done", nullable = false)
    private boolean isDone = false;

    @Column(name = "scope", length = 50)
    private String scope;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10)
    private Priority priority;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiPlan> aiPlans = new ArrayList<>();

    public void updateTitle(String title) {this.title = title;}
    public void updateDeadline(LocalDate deadline) {this.deadline = deadline.atStartOfDay();}
    public void updateScope(String scope) {this.scope = scope;}
    public void updatePriority(Priority priority) {this.priority = priority;}

    @Builder(access = AccessLevel.PRIVATE)
    private Plan(
            User user,
            final String title,
            final LocalDateTime deadline,
            final String scope,
            final Priority priority
    ){
        this.user = user;
        this.title = title;
        this.deadline = deadline;
        this.scope = scope;
        this.priority = priority;
    }

    public static Plan create(User user, final String title, final LocalDateTime deadline, final String scope, final Priority priority) {
        return Plan.builder()
                .user(user)
                .title(title)
                .deadline(deadline)
                .scope(scope)
                .priority(priority)
                .build();
    }

}
