package com.miruni.backend.domain.user.validator;

import com.miruni.backend.domain.user.dto.request.UserSignupRequest;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    /**
     * 약관 동의 검증
     */
    public void validateAgreements(UserSignupRequest request) {
        validateAgreements(request.serviceAgreed(), request.privacyAgreed());
    }

    /**
     * 약관 동의 검증 (필드 기반)
     */
    public void validateAgreements(Boolean serviceAgreed, Boolean privacyAgreed) {
        // 서비스 이용약관은 필수
        if (serviceAgreed == null || !serviceAgreed) {
            throw BaseException.type(UserErrorCode.AGREEMENT_REQUIRED);
        }
    }
}
