package com.miruni.backend.domain.fcm.service;

import com.miruni.backend.domain.fcm.entity.FcmToken;
import com.miruni.backend.domain.fcm.exception.FcmErrorCode;
import com.miruni.backend.domain.fcm.repository.FcmTokenRepository;
import com.miruni.backend.global.exception.BaseException;
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

    public FcmToken getTokenByToken(String token){
        return fcmTokenRepository.findByToken(token)
                .orElseThrow(() -> BaseException.type(FcmErrorCode.NOT_FOUND_FCM_TOKEN));
    }
}
