package com.miruni.backend.domain.user.entity;

import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "survey")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 어떤 상황에 미루나요 (복수 선택 가능) - 비트마스크
    @Column(name = "delay_situation_mask", nullable = false)
    private long delaySituationMask;

    // 미루는 정도 (단일 선택)
    @Enumerated(EnumType.STRING)
    @Column(name = "delay_level", nullable = false, length = 20)
    private DelayLevel delayLevel;

    // 어떤 이유에 미루나요 (복수 선택 가능) - 비트마스크
    @Column(name = "delay_reason_mask", nullable = false)
    private long delayReasonMask;

    // === 정적 팩토리 ===
    public static Survey create(User user, Set<DelaySituation> situations, DelayLevel level, Set<DelayReason> reasons) {
        long situationMask = DelaySituation.createMask(situations);
        long reasonMask = DelayReason.createMask(reasons);

        return Survey.builder()
                .user(user)
                .delaySituationMask(situationMask)
                .delayLevel(level)
                .delayReasonMask(reasonMask)
                .build();
    }

    // === 수정 로직 ===
    public void update(Set<DelaySituation> situations, DelayLevel level, Set<DelayReason> reasons) {
        this.delaySituationMask = DelaySituation.createMask(situations);
        this.delayLevel = level;
        this.delayReasonMask = DelayReason.createMask(reasons);
    }
}

