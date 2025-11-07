package com.miruni.backend.domain.user.entity;

import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "survey")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 상황에 미루나요 (복수 선택 가능)
    @Enumerated(EnumType.STRING)
    @Column(name = "delay_situation", nullable = false)
    private DelaySituation delaySituation;

    // 미루는 정도 (1~5)
    @Column(name = "delay_range", nullable = false)
    @Min(1) @Max(5)
    private int delayRange;

    // 어떤 이유에 미루나요 (복수 선택 가능)
    @Enumerated(EnumType.STRING)
    @Column(name = "delay_reason", nullable = false)
    private DelayReason delayReason;

}
