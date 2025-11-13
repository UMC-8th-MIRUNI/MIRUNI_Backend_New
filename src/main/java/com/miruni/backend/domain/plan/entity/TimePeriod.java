package com.miruni.backend.domain.plan.entity;

public enum TimePeriod {
    RANDOM, // 랜덤설정
    MORNING,    // 아침 시간(6~9시)
    FOCUS_MORNING, // 오전 집중 시간(9~12시)
    AFTERNOON, // 오후 느슨한 시간 (13~17시)
    EVENING, // 저녁 시간 (18~21시)
    NIGHT, // 밤 시간 (22~24시)
    DAWN // 새벽 (0~6시)
}
