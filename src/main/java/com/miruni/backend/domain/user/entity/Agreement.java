package com.miruni.backend.domain.user.entity;

import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "agreement")
public class Agreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 이용약관 동의 (true만 가능)
    @Column(name = "service_agreed", nullable = false)
    private boolean serviceAgreed;

    // 개인정보 수집 동의 (true만 가능)
    @Column(name = "privacy_agreed")
    private Boolean privacyAgreed;

    // 마케팅 동의 (true/false 수정 가능)
    @Column(name = "marketing_agreed")
    private Boolean marketingAgreed;

}
