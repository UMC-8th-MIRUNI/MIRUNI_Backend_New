package com.miruni.backend.domain.fcm.service;

import com.miruni.backend.domain.fcm.dto.command.RegisterTokenCommandDto;
import com.miruni.backend.domain.fcm.dto.response.RegisterTokenResponseDto;
import com.miruni.backend.domain.fcm.entity.FcmToken;
import com.miruni.backend.domain.fcm.repository.FcmTokenRepository;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmTokenCommandService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserQueryService userQueryService;

    @Transactional
    public RegisterTokenResponseDto registerToken(RegisterTokenCommandDto command){

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

        return new RegisterTokenResponseDto(fcmToken.getId());
    }

}
