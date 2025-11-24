package com.miruni.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.miruni.backend.domain.user.exception.UserErrorCode;
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
public class EmailVerificationService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmailVerificationProperties emailVerificationProperties;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원가입 시 이메일로 인증코드를 발송하고, Redis에 5분 동안 저장합니다.
     */
    public void sendSignUpVerificationCode(String email) {

        String code = generateSignUpVerificationCode();

        // Redis에 인증코드 저장 (5분 TTL)
        String key = emailVerificationProperties.prefix() + email;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(emailVerificationProperties.expireMinutes()));

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom("noreply@miruni.com", "Miruni");
            helper.setTo(email);
            helper.setSubject("🔐 Miruni 회원가입 인증 코드");
            
            String htmlContent = loadEmailTemplate(code);
            helper.setText(htmlContent, true);

            // 이메일 전송
            javaMailSender.send(mimeMessage);
            log.info("회원가입 이메일 인증 코드를 {}로 성공적으로 전송했습니다: {}", email, code);

        } catch (Exception e) {
            log.error("이메일 인증코드 발송 실패: 이메일: {}, 오류: {}", email, e.getMessage(), e);
            throw BaseException.type(UserErrorCode.EMAIL_VERIFICATION_FAILED);
        }
        
    }

    private String loadEmailTemplate(String code) {
        try {
            Context context = new Context();
            context.setVariable("code", code);
            
            return templateEngine.process("email", context);
        } catch (Exception e) {
            log.error("이메일 템플릿 로드 실패: {}", e.getMessage(), e);
            return createSimpleEmailTemplate(code);
        }
    }

    private String createSimpleEmailTemplate(String code) {
        try{
            Context context = new Context();
            context.setVariable("code", code);
            return templateEngine.process("mailTemplate", context);
        }catch(Exception e){
            return "Miruni 회원가입 인증 코드: " + code;
        }
    }

    /**
     * 사용자가 입력한 인증코드를 검증합니다.
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
}

