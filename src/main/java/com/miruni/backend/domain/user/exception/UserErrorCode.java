package com.miruni.backend.domain.user.exception;

import com.miruni.backend.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum 
UserErrorCode implements ErrorCode {
    
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER404_1", "이미 사용 중인 닉네임입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER409_2", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_3", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER400_4", "비밀번호가 올바르지 않습니다."),
    AGREEMENT_REQUIRED(HttpStatus.BAD_REQUEST, "USER400_5", "필수 약관에 동의해야 합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "USER401_6", "유효하지 않은 토큰입니다."),
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "USER400_7", "이미 탈퇴한 사용자입니다."),
    EMAIL_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER500_8", "이메일 인증 코드 발송에 실패했습니다."),
    EMAIL_VERIFICATION_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER400_9", "이메일 인증 코드가 존재하지 않거나 만료되었습니다."),
    EMAIL_VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "USER400_10", "이메일 인증 코드가 올바르지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "USER400_11", "이메일 인증이 완료되지 않았습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

