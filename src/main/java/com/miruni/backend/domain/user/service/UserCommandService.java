package com.miruni.backend.domain.user.service;

import com.miruni.backend.domain.user.dto.request.SurveyRequest;
import com.miruni.backend.domain.user.dto.request.UserSignupRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.SurveyResponse;
import com.miruni.backend.domain.user.dto.command.ProfileUpdateCommandDto;
import com.miruni.backend.domain.user.dto.command.UserInfoUpdateCommandDto;
import com.miruni.backend.domain.user.dto.request.ResetPasswordRequest;
import com.miruni.backend.domain.user.dto.request.UserSignupRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.UserInfoResponseDto;
import com.miruni.backend.domain.user.entity.Agreement;
import com.miruni.backend.domain.user.entity.Survey;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.AgreementRepository;
import com.miruni.backend.domain.user.repository.SurveyRepository;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.domain.user.validator.UserValidator;
import com.miruni.backend.global.authroize.TokenService;
import com.miruni.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    
    private final UserRepository userRepository;
    private final AgreementRepository agreementRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final TokenService tokenService;
    private final SurveyRepository surveyRepository;
    private final VerificationService verificationService;
    
    /**
     * 일반 회원가입
     */
    public JwtResponseDto signup(UserSignupRequest request) {
        // 이메일 인증 여부 확인
        verificationService.assertSignUpEmailVerified(request.email());

        // 이메일 중복 체크
        validateEmailNotExists(request.email());
        
        // 닉네임 중복 체크
        validateNicknameNotExists(request.nickname());
        
        // 전화번호 중복 체크
        validatePhoneNumberNotExists(request.phoneNumber());
        
        // 필수 약관 동의 체크 (서비스 이용약관만 필수)
        userValidator.validateAgreements(request.serviceAgreed());
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());
        
        // User 엔티티 생성 및 저장 
        User user = User.createNormalUser(request.email(), encodedPassword, request.nickname());
        userRepository.save(user);
        
        // Agreement 엔티티 생성 및 저장
        Agreement agreement = Agreement.create(user, request.serviceAgreed(), request.privacyAgreed(), request.marketingAgreed());
        agreementRepository.save(agreement);
        
        // JWT 토큰 생성 및 반환
        return tokenService.issueTokenResponse(user);
    }

    /** 
     * 이메일 중복 검증
     */
    private void validateEmailNotExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw BaseException.type(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    /**
     * 닉네임 중복 검증
     */
    private void validateNicknameNotExists(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw BaseException.type(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    /**
     * 전화번호 중복 검증
     */
    private void validatePhoneNumberNotExists(String phoneNumber) {
        // 하이픈 제거 후 검증
        String normalizedPhoneNumber = phoneNumber.replace("-", "");
        if (userRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
            throw BaseException.type(UserErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
        }
    }

    /**
     * 회원 탈퇴
     */
    public void withdrawUser(String accessToken, Long userId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        // 이미 탈퇴한 사용자인지 확인
        if (user.isDeleted()) {
            throw BaseException.type(UserErrorCode.USER_ALREADY_DELETED);
        }

        // 소프트 삭제 처리
        user.delete();

        // 토큰 무효화 (로그아웃 처리)
        tokenService.logout(accessToken, userId);

        log.info("회원 탈퇴 완료: userId={}", userId);
    }

    /**
     * 설문조사 수정/저장
     */
    public SurveyResponse updateSurvey(SurveyRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        // 기존 설문이 있으면 수정, 없으면 새로 생성
        Survey survey = surveyRepository.findByUserId(userId);

        if (survey == null) {
            survey = Survey.create(
                    user,
                    request.situations(),
                    request.level(),
                    request.reasons()
            );
            surveyRepository.save(survey);
        } else {
            // 기존 설문 업데이트 (JPA 더티 체킹)
            survey.update(
                    request.situations(),
                    request.level(),
                    request.reasons()
            );
        }

        log.info("설문조사 수정: userId={}, situations={}, level={}, reasons={}",
                userId, request.situations(), request.level(), request.reasons());

        return SurveyResponse.of(
                "설문조사가 수정되었습니다!",
                LocalDateTime.now(),
                "UPDATED"
        );
    }
}
