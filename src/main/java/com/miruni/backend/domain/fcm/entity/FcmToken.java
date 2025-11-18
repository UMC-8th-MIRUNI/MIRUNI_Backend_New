package com.miruni.backend.domain.fcm.entity;

import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm_token")
public class FcmToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 일정 5분 전 알람
    @Column(name = "before_5min_alarm", nullable = false)
    private boolean before5minAlarm;

    // 일정 10분 전 알람
    @Column(name = "before_10min_alarm", nullable = false)
    private boolean before10minAlarm;

    // 일정 시작 팝업 알람
    @Column(name = "popup_alarm", nullable = false)
    private boolean popupAlarm;

    // 일정 미뤘을 시 잔소리 알람
    @Column(name = "nag_alarm", nullable = false)
    private boolean nagAlarm;

    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Builder
    private FcmToken(final User user,
                     final boolean before5minAlarm,
                     final boolean before10minAlarm,
                     final boolean popupAlarm,
                     final boolean nagAlarm,
                     final String deviceId,
                     final String token) {
        this.user = user;
        this.before5minAlarm = before5minAlarm;
        this.before10minAlarm = before10minAlarm;
        this.popupAlarm = popupAlarm;
        this.nagAlarm = nagAlarm;
        this.deviceId = deviceId;
        this.token = token;
    }

    public static FcmToken create(final User user,
                                  final boolean before5minAlarm,
                                  final boolean before10minAlarm,
                                  final boolean popupAlarm,
                                  final boolean nagAlarm,
                                  final String deviceId,
                                  final String token) {
        return FcmToken.builder()
                .user(user)
                .before5minAlarm(before5minAlarm)
                .before10minAlarm(before10minAlarm)
                .popupAlarm(popupAlarm)
                .nagAlarm(nagAlarm)
                .deviceId(deviceId)
                .token(token)
                .build();
    }
}
