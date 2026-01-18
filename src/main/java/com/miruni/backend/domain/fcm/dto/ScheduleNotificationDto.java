package com.miruni.backend.domain.fcm.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ScheduleNotificationDto(

        Long userId,

        Long targetId,

        String planType,

        String alarmType,

        LocalDateTime notificationTime,

        String taskTitle
) {
}
