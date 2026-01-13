package com.miruni.backend.domain.user.service;

import com.miruni.backend.domain.user.dto.AuthUserInfoDto;
import com.miruni.backend.domain.user.dto.request.GoogleLoginRequest;
import com.miruni.backend.domain.user.dto.request.KakaoLoginRequest;
import com.miruni.backend.domain.user.dto.request.LoginRequest;
import com.miruni.backend.domain.user.dto.request.SocialSignupCompleteRequest;
import com.miruni.backend.domain.user.dto.response.JwtResponseDto;
import com.miruni.backend.domain.user.dto.response.SocialLoginResponseDto;
import com.miruni.backend.domain.user.entity.Agreement;
import com.miruni.backend.domain.user.entity.OauthProvider;
import com.miruni.backend.domain.user.entity.User;
import com.miruni.backend.domain.user.entity.UserRole;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.domain.user.repository.AgreementRepository;
import com.miruni.backend.domain.user.repository.UserRepository;
import com.miruni.backend.domain.user.validator.UserValidator;
import com.miruni.backend.global.authroize.TokenService;
import com.miruni.backend.global.client.GoogleAuthClient;
import com.miruni.backend.global.client.KakaoAuthClient;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

    private final UserRepository userRepository;
    private final AgreementRepository agreementRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserValidator userValidator;
    private final GoogleAuthClient googleAuthClient;
    private final KakaoAuthClient kakaoAuthClient;

    /**
     * 일반 로그인
     */
    public JwtResponseDto login(LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw BaseException.type(UserErrorCode.INVALID_PASSWORD);
        }

        // 토큰 발급
        log.info("로그인 성공: email={}, userId={}", request.email(), user.getId());
        return tokenService.issueTokenResponse(user);
    }

    /**
     *  일반 로그아웃
     */
    public void logout(String accessToken, Long userId) {
        tokenService.logout(accessToken, userId);
    }

    /**
     * 액세스/리프레시 토큰 재발급
     */
    public JwtResponseDto reissueToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        return tokenService.reissueToken(user, refreshToken);
    }

    /**
     * 구글 소셜 로그인
     */
    public SocialLoginResponseDto loginWithGoogle(GoogleLoginRequest request) {
        AuthUserInfoDto googleUserInfo = googleAuthClient.getUserInfoByIdToken(request.googleIdToken());
        return processSocialLogin(googleUserInfo, OauthProvider.Google);
    }

    /**
     * 카카오 소셜 로그인
     */
    public SocialLoginResponseDto loginWithKakao(KakaoLoginRequest request) {
        AuthUserInfoDto kakaoUserInfo = kakaoAuthClient.getUserInfoByAccessToken(request.accessToken());
        return processSocialLogin(kakaoUserInfo, OauthProvider.Kakao);
    }

    /**
     * 소셜 로그인 공통 처리 로직 (가입 미완료는 ROLE_GUEST(PENDING_SIGNUP)로 구분)
     */
    private SocialLoginResponseDto processSocialLogin(AuthUserInfoDto userInfo, OauthProvider provider) {
        Optional<User> optionalUser = userRepository.findByEmail(userInfo.email());
        boolean isNewUser = optionalUser.isEmpty();

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 다른 소셜 제공자로 이미 가입된 이메일인 경우
            if (user.getOauthProvider() != null && user.getOauthProvider() != provider) {
                throw BaseException.type(UserErrorCode.OAUTH_PROVIDER_MISMATCH);
            }

            if (user.isDeleted()) {
                user.restore();
            }

            boolean hasAgreements = agreementRepository.existsByUserId(user.getId());

            // 약관까지 완료된 유저면 ROLE_USER 토큰으로 바로 로그인 처리
            if (hasAgreements && user.getRole() == UserRole.USER) {
                JwtResponseDto tokens = tokenService.issueTokenResponse(user);
                return SocialLoginResponseDto.loggedIn(tokens, false);
            }

            // 약관 미완료 / 가입 미완료(ROLE_GUEST)인 경우 → 토큰 발급 후 signupRequired = true
            JwtResponseDto guestTokens = tokenService.issueTokenResponse(user);
            return SocialLoginResponseDto.signupNeeded(guestTokens, false);
        }

        // 신규 소셜 유저 생성 (ROLE_GUEST(PENDING_SIGNUP), 임시 닉네임/비밀번호)
        User newUser = createPendingSocialUser(userInfo, provider);
        userRepository.save(newUser);

        JwtResponseDto guestTokens = tokenService.issueTokenResponse(newUser);
        return SocialLoginResponseDto.signupNeeded(guestTokens, isNewUser);
    }

    private User createPendingSocialUser(AuthUserInfoDto userInfo, OauthProvider provider) {
        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);
        String tempNickname = generateTempNickname(provider);

        return User.createSocialGuest(
                userInfo.name(),
                userInfo.email(),
                encodedPassword,
                tempNickname,
                provider
        );
    }

    private String generateTempNickname(OauthProvider provider) {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return provider.name().toLowerCase() + "_" + suffix;
    }

    /**
     * 소셜 회원가입 완료 (ROLE_GUEST(PENDING_SIGNUP) → ROLE_USER)
     */
    public JwtResponseDto completeSocialSignup(
            OauthProvider provider,
            Long userId,
            SocialSignupCompleteRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        // 소셜 제공자 일치 여부 확인
        if (user.getOauthProvider() != null && user.getOauthProvider() != provider) {
            throw BaseException.type(UserErrorCode.OAUTH_PROVIDER_MISMATCH);
        }

        // 이미 약관/회원가입이 완료된 경우 -> 바로 토큰 재발급
        if (agreementRepository.existsByUserId(user.getId()) && user.getRole() == UserRole.USER) {
            return tokenService.issueTokenResponse(user);
        }

        // 약관 검증
        userValidator.validateAgreements(request.serviceAgreed(), request.privacyAgreed());

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.nickname())) {
            throw BaseException.type(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 프로필/약관 업데이트 및 ROLE_USER 전환
        user.updateNickname(request.nickname());
        user.changeRole(UserRole.USER);

        Agreement agreement = Agreement.create(
                user,
                request.serviceAgreed(),
                request.privacyAgreed(),
                request.marketingAgreed()
        );
        agreementRepository.save(agreement);

        // 최종 JWT 토큰 발급 (ROLE_USER)
        return tokenService.issueTokenResponse(user);
    }

}

