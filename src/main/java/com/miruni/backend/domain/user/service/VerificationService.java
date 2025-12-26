package com.miruni.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.miruni.backend.domain.user.dto.request.EmailVerificationVerifyRequest;
import com.miruni.backend.domain.user.dto.response.VerifyResponse;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.properties.EmailVerificationProperties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmailVerificationProperties emailVerificationProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    /**
     * 비밀번호 재설정 코드 검증
     * - 인증코드를 검증하고, 검증 완료 토큰을 생성하여 반환
    */
    public VerifyResponse verifyPasswordResetCode(EmailVerificationVerifyRequest request) {
        String key = emailVerificationProperties.prefix() + request.email();
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw BaseException.type(UserErrorCode.EMAIL_VERIFICATION_CODE_NOT_FOUND);
        }

        if (!storedCode.equals(request.code())) {
            throw BaseException.type(UserErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        // 검증 완료 토큰 생성
        String resetToken = generateResetToken(request.email());
        String tokenKey = "reset_token:" + resetToken;
        redisTemplate.opsForValue().set(tokenKey, request.email(), Duration.ofMinutes(5));
                
        log.info("인증 코드 검증 성공 - 이메일: {}, 토큰: {}", request.email(), resetToken);
        return new VerifyResponse(resetToken);
    }

    /**
     * 비밀번호 재설정 토큰 소비
     * - 유효한 resetToken이면 이메일을 반환하고, 토큰은 한 번만 사용되도록 삭제
     */
    public String consumeResetToken(String resetToken) {
        String tokenKey = "reset_token:" + resetToken;
        String email = redisTemplate.opsForValue().get(tokenKey);

        if (email == null) {
            throw BaseException.type(UserErrorCode.INVALID_TOKEN);
        }

        // 토큰은 한 번 사용 후 삭제
        redisTemplate.delete(tokenKey);

        log.info("비밀번호 재설정 토큰 소비 완료: email={}", email);
        return email;
    }

    /**
     * 회원가입 시 이메일로 인증코드를 발송하고, Redis에 TTL 동안 저장
     */
    public void sendSignUpVerificationCode(String email) {

        if (userRepository.existsByEmail(email)) {
            throw BaseException.type(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        sendVerificationCode(email, VerificationType.SIGN_UP);
    }

    /**
     * 비밀번호 재설정용 인증 코드 요청
     * - 외부 응답은 항상 동일하게 성공처럼 보이되, 내부에서는 비밀번호 재설정이 가능한 계정에만 메일을 발송
     */
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        // 1) 유저 없음
        // 2) 탈퇴(소프트 삭제)된 유저
        // 3) 소셜 로그인 전용 계정 (정책상 비밀번호 재설정 불가라고 가정)
        if (user == null || user.isDeleted() || user.getOauthProvider() != null) {
            log.info("비밀번호 재설정 대상이 아닌 계정 요청: email={}", email);
            
            // 클라이언트에는 항상 동일한 응답을 주기 위해 예외를 던지지 않고 조용히 종료
            return;
        }

        // 여기서만 실제 인증코드 생성 + Redis 저장 + 메일 발송
        sendVerificationCode(user.getEmail(), VerificationType.PASSWORD_RESET);


        // sendVerificationCode(email, VerificationType.PASSWORD_RESET);


    }

    /**
     * 공통 이메일 인증 코드 발송 로직
     */
    private void sendVerificationCode(String email, VerificationType type) {

        String code = switch (type) {
            case SIGN_UP -> generateSignUpVerificationCode();
            case PASSWORD_RESET -> generatePasswordResetCode();
        };

        // Redis에 인증코드 저장 (TTL 분)
        String key = emailVerificationProperties.prefix() + email;
        redisTemplate.opsForValue()
                .set(key, code, Duration.ofMinutes(emailVerificationProperties.expireMinutes()));

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("noreply@miruni.com", "Miruni");
            helper.setTo(email);
            helper.setSubject(type.subject);

            String htmlContent = loadEmailTemplate(code, type);
            helper.setText(htmlContent, true);

            // 이메일 전송
            javaMailSender.send(mimeMessage);
            log.info("{} 인증 코드를 {}로 성공적으로 전송했습니다: {}", type.logPrefix, email, code);

        } catch (Exception e) {
            log.error("{} 인증코드 발송 실패: 이메일: {}, 오류: {}", type.logPrefix, email, e.getMessage(), e);
            throw BaseException.type(UserErrorCode.EMAIL_VERIFICATION_FAILED);
        }
    }

    private String loadEmailTemplate(String code, VerificationType type) {
        try {
            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("title", type.subject);
            context.setVariable("description", type.description);

            return templateEngine.process("email", context);
        } catch (Exception e) {
            log.error("이메일 템플릿 로드 실패: {}", e.getMessage(), e);
            return createSimpleEmailTemplate(code, type);
        }
    }

    private String createSimpleEmailTemplate(String code, VerificationType type) {
        try{
            Context context = new Context();
            context.setVariable("code", code);
            return templateEngine.process("mailTemplate", context);
        }catch(Exception e){
            return type.fallbackMessage + ": " + code;
        }
    }

    /**
     * 회원가입 시 사용자가 입력한 인증코드를 검증합니다.
     */
    public void verifySignUpVerificationCode(String email, String code) {
        String key = emailVerificationProperties.prefix() + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            // 코드가 없으면 요청 이력 없거나 만료된 것
            throw BaseException.type(UserErrorCode.EMAIL_VERIFICATION_CODE_NOT_FOUND);
        }

        if (!storedCode.equals(code)) {
            throw BaseException.type(UserErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        // 성공 시 한 번만 쓰도록 삭제
        redisTemplate.delete(key);

        // 회원가입용 이메일 인증 완료 플래그 저장 (TTL 동일 적용)
        markSignUpEmailVerified(email);

        log.info("이메일 인증 성공: email={}", email);
    }

    private String generateSignUpVerificationCode() {
        // 대문자 알파벳 + 숫자로 구성된 6자리 코드 생성
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int idx = random.nextInt(chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    private String generatePasswordResetCode() {
        // 4자리 숫자로 구성된 코드 생성
        final String chars = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            int idx = random.nextInt(chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    private String generateResetToken(String email) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String combined = email + ":" + timestamp;
        
        return java.util.Base64.getEncoder().encodeToString(combined.getBytes()).substring(0, 32);
    }

    /**
     * 회원가입용 이메일 인증 완료 플래그 저장
     */
    public void markSignUpEmailVerified(String email) {
        String verifiedKey = getSignupVerifiedKey(email);
        redisTemplate.opsForValue()
                .set(verifiedKey, "true", Duration.ofMinutes(emailVerificationProperties.expireMinutes()));
        log.info("회원가입 이메일 인증 완료 플래그 저장: email={}", email);
    }

    /**
     * 회원가입 시 이메일이 사전에 인증되었는지 검증
     */
    public void assertSignUpEmailVerified(String email) {
        String verifiedKey = getSignupVerifiedKey(email);
        String verified = redisTemplate.opsForValue().get(verifiedKey);

        if (!"true".equals(verified)) {
            log.warn("이메일 인증이 완료되지 않은 상태에서 회원가입 시도: email={}", email);
            throw BaseException.type(UserErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    private String getSignupVerifiedKey(String email) {
        return emailVerificationProperties.prefix() + "signup:verified:" + email;
    }

    /**
     * 이메일 인증 타입 정의
     */
    private enum VerificationType {
        SIGN_UP(
                "회원가입 이메일",
                "🔐 Miruni 회원가입 인증 코드",
                "회원가입 시 인증을 위한 코드입니다.",
                "Miruni 회원가입 인증 코드"
        ),
        PASSWORD_RESET(
                "비밀번호 재설정",
                "🔐 Miruni 비밀번호 재설정 인증 코드",
                "비밀번호 재설정을 위한 인증 코드입니다.",
                "Miruni 비밀번호 재설정 인증 코드"
        );

        private final String logPrefix;
        private final String subject;
        private final String description;
        private final String fallbackMessage;

        VerificationType(String logPrefix, String subject, String description, String fallbackMessage) {
            this.logPrefix = logPrefix;
            this.subject = subject;
            this.description = description;
            this.fallbackMessage = fallbackMessage;
        }
    }
}

