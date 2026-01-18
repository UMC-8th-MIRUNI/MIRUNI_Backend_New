package com.miruni.backend.domain.fcm.scheduler;

import com.miruni.backend.domain.fcm.dto.ScheduleNotificationDto;
import com.miruni.backend.domain.fcm.repository.NotificationRedisRepository;
import com.miruni.backend.domain.fcm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduleRecovery {

    private final NotificationRedisRepository scheduleRedisRepository;
    private final NotificationService notificationService;

    @EventListener(ApplicationReadyEvent.class)
    public void recover() {
        log.info("알림 스케줄 복구 시작");

        List<ScheduleNotificationDto> schedules = scheduleRedisRepository.getAll();
        int recovered = 0;
        int skipped = 0;

        for (ScheduleNotificationDto info : schedules) {
            if (info.notificationTime().isAfter(LocalDateTime.now())) {
                notificationService.reschedule(info);
                recovered++;
            } else {
                skipped++;
            }
        }

        log.info("알림 스케줄 복구 완료: 복구={}, 스킵(만료)={}", recovered, skipped);
    }
}