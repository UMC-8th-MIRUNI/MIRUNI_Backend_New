package com.miruni.backend.domain.fcm.controller;

import com.miruni.backend.domain.fcm.dto.request.RegisterTokenRequestDto;
import com.miruni.backend.domain.fcm.dto.request.UpdateTokenRequestDto;
import com.miruni.backend.global.authroize.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "FCM", description = "FCM 푸시 알림 관리 API")
public interface FcmApi {

    @Operation(
            summary = "FCM 토큰 등록",
            description = """
                    사용자의 FCM 토큰을 등록하여 푸시 알림을 받을 수 있도록 설정합니다.
            
                    주의사항:
                    - JWT 인증이 필요합니다
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    void registerToken(
            @Valid @RequestBody RegisterTokenRequestDto request,
            @LoginUser Long userId);

    @Operation(
            summary = "FCM 토큰 업데이트",
            description = """
                    사용자의 FCM 토큰을 업데이트하여 
                    푸시 알림을 설정을 수정할수 있도록 설정합니다.
            
                    주의사항:
                    - JWT 인증이 필요합니다
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "잘못된 요청")
    })
    void updateToken(
            @Valid @RequestBody UpdateTokenRequestDto request,
            @PathVariable String deviceId,
            @LoginUser Long userId
    );
}
