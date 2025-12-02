package com.miruni.backend.domain.user.validator;

import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    /**
     * 서비스 이용약관 동의 여부만 검증 (필수)
     */
    public void validateAgreements(Boolean serviceAgreed) {
        if (serviceAgreed == null || !serviceAgreed) {
            throw BaseException.type(UserErrorCode.AGREEMENT_REQUIRED);
        }
    }
}
