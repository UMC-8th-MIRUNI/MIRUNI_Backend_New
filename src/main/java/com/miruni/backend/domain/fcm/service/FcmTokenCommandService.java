package com.miruni.backend.domain.fcm.service;

import com.miruni.backend.domain.fcm.dto.command.RegisterTokenCommandDto;
import com.miruni.backend.domain.fcm.dto.command.UpdateTokenCommandDto;
import com.miruni.backend.domain.fcm.entity.FcmToken;
import com.miruni.backend.domain.fcm.exception.FcmErrorCode;
import com.miruni.backend.domain.fcm.repository.FcmTokenRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmTokenCommandService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserQueryService userQueryService;

    @Transactional
    public void registerToken(RegisterTokenCommandDto command){

        User user = userQueryService.getUserById(command.userId());

        FcmToken fcmToken = FcmToken.create(
                user,
                command.before5minAlarm(),
                command.before10minAlarm(),
                command.popupAlarm(),
                command.nagAlarm(),
                command.deviceId(),
                command.token());

        fcmTokenRepository.save(fcmToken);
    }

    @Transactional
    public void updateToken(UpdateTokenCommandDto command){
        FcmToken fcmToken = fcmTokenRepository.findByDeviceIdAndUserId(command.deviceId(), command.userId())
                .orElseThrow(() -> BaseException.type(FcmErrorCode.NOT_FOUND_FCM_TOKEN));

        fcmToken.updateAlarm(
                command.before5minAlarm(),
                command.before10minAlarm(),
                command.popupAlarm(),
                command.nagAlarm());
    }
}
