package com.miruni.backend.domain.fcm.service;

import com.miruni.backend.domain.fcm.entity.FcmToken;
import com.miruni.backend.domain.fcm.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmTokenQueryService {

    FcmTokenRepository fcmTokenRepository;

    public List<FcmToken> getTokensByUserId(Long userId){
        return fcmTokenRepository.findByUserId(userId);
    };
}
