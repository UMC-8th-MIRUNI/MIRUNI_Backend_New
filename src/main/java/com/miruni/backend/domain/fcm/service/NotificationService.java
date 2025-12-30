package com.miruni.backend.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.miruni.backend.domain.fcm.entity.FcmToken;
import com.miruni.backend.domain.fcm.exception.FcmErrorCode;
import com.miruni.backend.domain.plan.entity.AiPlan;
import com.miruni.backend.domain.plan.entity.BasicPlan;
import com.miruni.backend.domain.plan.entity.Status;
import com.miruni.backend.domain.plan.service.AiPlanQueryService;
import com.miruni.backend.domain.plan.service.BasicPlanQueryService;
import com.miruni.backend.domain.plan.service.PlanQueryService;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TaskScheduler taskScheduler;

    // 스케줄된 작업들을 추적하는 맵
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final FcmTokenQueryService fcmTokenQueryService;
    private final FirebaseMessaging firebaseMessaging;
    private final BasicPlanQueryService basicPlanQueryService;
    private final AiPlanQueryService aiPlanQueryService;

    //Plan 알림 등록
    public void scheduleNotification(BasicPlan plan){

        LocalDateTime scheduledTime = validateAndGetScheduledTime(
                plan.getStatus(),
                plan.getId(),
                LocalDateTime.of(plan.getScheduledDate(), plan.getScheduledTime())
        );

        if (scheduledTime == null) return;

        scheduleAllNotifications(
                NotificationTask
                        .builder()
                        .userId(plan.getUser().getId())
                        .targetId(plan.getId())
                        .type(PlanType.BASIC_PLAN)
                        .scheduledTime(scheduledTime)
                        .taskTitle(plan.getTitle())
                        .build()
        );


        log.info("Plan 알림 스케줄 등록 완료: planId = {}, startTime = {}", plan.getId(), plan.getScheduledTime());
    }

    //AIPlan 알림 등록
    public void scheduleNotification(AiPlan aiplan){

        LocalDateTime scheduledTime = validateAndGetScheduledTime(
                aiplan.getStatus(),
                aiplan.getId(),
                LocalDateTime.of(aiplan.getScheduledDate(), aiplan.getScheduledTime())
        );

        if (scheduledTime == null) return;


        scheduleAllNotifications(
                NotificationTask
                        .builder()
                        .userId(aiplan.getPlan().getUser().getId())
                        .targetId(aiplan.getId())
                        .type(PlanType.AI_PLAN)
                        .scheduledTime(scheduledTime)
                        .taskTitle(aiplan.getSubTitle())
                        .build()
        );

        log.info("AiPlan 알림 스케줄 등록 완료: planId = {}, startTime = {}", aiplan.getId(), aiplan.getScheduledTime());
    }


    //활성화 토큰 여부 확인
    private void scheduleAllNotifications(NotificationTask task) {
        List<FcmToken> fcmTokens = fcmTokenQueryService.getTokensByUserId(task.userId());

        for (FcmToken fcmToken : fcmTokens) {
            scheduleAlarm(task, AlarmType.BEFORE_5MIN, fcmToken.getToken());

            scheduleAlarm(task, AlarmType.BEFORE_10MIN, fcmToken.getToken());

            scheduleAlarm(task, AlarmType.POPUP, fcmToken.getToken());

            scheduleAlarm(task, AlarmType.NAG, fcmToken.getToken());
        }
    }


    //스케줄러에 알람 등록
    private void scheduleAlarm(NotificationTask task, AlarmType alarmType, String token) {
        LocalDateTime notificationTime = calculateTime(task.scheduledTime, alarmType);

        String scheduleKey = createScheduleKey(task.type(), task.targetId(), alarmType, token);


        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> executeNotification(task, alarmType, token),
                notificationTime.atZone(ZoneId.systemDefault()).toInstant()
        );

        scheduledTasks.put(scheduleKey, future);
    }

    //알람 실행 메서드
    private void executeNotification(NotificationTask task, AlarmType alarmType, String token) {
        try {
            if (!isTaskTodo(task))
                return;

            if(!canReceiveAlarm(alarmType, token))
                return;

            sendNotification(task, alarmType, token);

        } catch (Exception e) {
            log.error("알림 전송 실패", e);
        } finally {
            String scheduleKey = createScheduleKey(task.type(), task.targetId(), alarmType, token);
            scheduledTasks.remove(scheduleKey);
        }
    }

    //시간계산
    private LocalDateTime calculateTime(LocalDateTime scheduledTime, AlarmType alarmType) {
        return switch (alarmType) {
            case BEFORE_5MIN -> scheduledTime.minusMinutes(5);
            case BEFORE_10MIN -> scheduledTime.minusMinutes(10);
            case POPUP -> scheduledTime;
            case NAG -> scheduledTime.plusMinutes(5);
        };
    }


    //제목 및 문구 만들기
    private NotificationContent createContent(String title, AlarmType alarmType) {
        return switch (alarmType) {
            case BEFORE_5MIN -> new NotificationContent(
                    String.format("5분 뒤에 '%s'가 예정되어 있어요!", title),
                    String.format("곧 '%s' 시간이에요!", title)
            );
            case BEFORE_10MIN -> new NotificationContent(
                    String.format("10분 뒤에 '%s'가 예정되어 있어요!", title),
                    String.format("10분 후 '%s' 일정을 시작하고, 땅콩 3개를 획득하세요!", title)
            );
            case POPUP -> new NotificationContent(
                    String.format("'%s'를 시작하세요!", title),
                    String.format("지금 바로 '%s' 일정을 시작하세요!", title)
            );
            case NAG -> new NotificationContent(
                    String.format("'%s'를 얼른 시작하세요!", title),
                    String.format("지금 바로 '%s' 일정을 시작하지 않으면, 땅콩을 잃어버릴거에요", title)
            );
        };
    }

    //스케줄키 생성
    private String createScheduleKey(PlanType type, Long targetId, AlarmType alarmType, String token) {
        return String.format("%s_%d_%s_%s", type.name(), targetId, alarmType.name(), token);
    }

    //실제 알람 전송
    private void sendNotification(NotificationTask task, AlarmType alarmType, String token) {
        NotificationContent content = createContent(task.taskTitle(), alarmType);

        try {
            Message message = Message.builder()
                    .putData("type", task.type().name())
                    .putData("targetId", task.targetId().toString())
                    .putData("alarmType", alarmType.name())
                    .setNotification(Notification.builder()
                            .setTitle(content.title)
                            .setBody(content.body)
                            .build())
                    .setToken(token)
                    .build();

            firebaseMessaging.send(message);

        }catch (FirebaseMessagingException e){
        throw BaseException.type(FcmErrorCode.FCM_SEND_FAILED);
        }
    }

    private LocalDateTime validateAndGetScheduledTime(Status status, Long planId, LocalDateTime scheduledTime) {
        if (status == Status.DONE) {
            throw BaseException.type(FcmErrorCode.ALREADY_FINISHED_TASK);
        }

        if (scheduledTime.isBefore(LocalDateTime.now())) {
            log.warn("이미 지난 일정: planId = {}, scheduledTime = {}", planId, scheduledTime);
            return null;
        }

        return scheduledTime;
    }

    private boolean isTaskTodo(NotificationTask task){
        if(task.type == PlanType.BASIC_PLAN){
            BasicPlan plan = basicPlanQueryService.getByPlanIdAndUserId(task.targetId, task.userId);
            return plan.getStatus() == Status.TODO;

        }
        else{
            AiPlan aiPlan = aiPlanQueryService.getByPlanIdAndUserId(task.targetId, task.userId);
            return aiPlan.getStatus() == Status.TODO;
        }
    }

    private boolean canReceiveAlarm(AlarmType alarmType, String token) {
        FcmToken fcmToken = fcmTokenQueryService.getTokenByToken(token);
        if (fcmToken == null) return false;

        return switch (alarmType) {
            case BEFORE_5MIN -> fcmToken.isBefore5minAlarm();
            case BEFORE_10MIN -> fcmToken.isBefore10minAlarm();
            case POPUP -> fcmToken.isPopupAlarm();
            case NAG -> fcmToken.isNagAlarm();
        };
    }

    // == 관련 record == //
    @Builder
    private record NotificationTask(
            Long userId,
            Long targetId,
            PlanType type,  // PLAN or AI_PLAN
            LocalDateTime scheduledTime,
            String taskTitle  // Plan의 title이나 AiPlan의 description
    ) {}

    private enum PlanType {
        BASIC_PLAN, AI_PLAN
    }

    private enum AlarmType {
        BEFORE_5MIN,
        BEFORE_10MIN,
        POPUP,
        NAG
    }

    private record NotificationContent(
            String title,
            String body
    ){}
}
