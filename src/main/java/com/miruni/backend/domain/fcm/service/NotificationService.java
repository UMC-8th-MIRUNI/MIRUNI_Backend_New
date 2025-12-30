package com.miruni.backend.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.miruni.backend.domain.plan.service.AiPlanQueryService;
import com.miruni.backend.domain.plan.service.BasicPlanQueryService;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class NotificationService {
    private final TaskScheduler taskScheduler;

    // 스케줄된 작업들을 추적하는 맵
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final FirebaseMessaging firebaseMessaging;
    private final BasicPlanQueryService basicPlanQueryService;
    private final AiPlanQueryService aiPlanQueryService;
}
