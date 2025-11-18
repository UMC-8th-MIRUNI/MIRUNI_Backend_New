package com.miruni.backend.domain.fcm.repository;

import com.miruni.backend.domain.fcm.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
}
