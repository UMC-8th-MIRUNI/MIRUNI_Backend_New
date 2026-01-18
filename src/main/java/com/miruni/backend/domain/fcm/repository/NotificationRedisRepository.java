package com.miruni.backend.domain.fcm.repository;

import com.miruni.backend.domain.fcm.dto.ScheduleNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationRedisRepository {

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private static final String KEY_PREFIX = "notification:schedule";

    public void save(String scheduleKey, ScheduleNotificationDto info){
        Duration ttl = Duration.between(LocalDateTime.now(), info.notificationTime())
                .plus(Duration.ofMinutes(10));

        if(ttl.isNegative() || ttl.isZero()) {
            log.warn("TTL이 0이하: key = {}", scheduleKey);
            return;
        }
        objectRedisTemplate.opsForValue().set(KEY_PREFIX + scheduleKey, info, ttl);
        log.debug("스케줄 저장: key = {}, ttl = {}", scheduleKey, ttl);
    }

    public void delete(String scheduleKey){
        objectRedisTemplate.delete(KEY_PREFIX + scheduleKey);
    }

    public ScheduleNotificationDto get(String scheduleKey){
        return (ScheduleNotificationDto) objectRedisTemplate.opsForValue()
                .get(KEY_PREFIX + scheduleKey);
    }

    public List<ScheduleNotificationDto> getAll(){
        Set<String> keys = objectRedisTemplate.keys(KEY_PREFIX + "*");
        if(keys.isEmpty()){
            return List.of();
        }

        return keys.stream()
                .map(key -> (ScheduleNotificationDto) objectRedisTemplate.opsForValue().get(key))
                .filter(Objects::nonNull)
                .toList();
    }
}
