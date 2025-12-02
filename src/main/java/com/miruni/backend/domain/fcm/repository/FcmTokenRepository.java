package com.miruni.backend.domain.fcm.repository;

import com.miruni.backend.domain.fcm.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByDeviceIdAndUserId(String deviceId, Long userId);

    boolean existsByDeviceId(String deviceId);

    List<FcmToken> findByUserId(Long userId);
}
