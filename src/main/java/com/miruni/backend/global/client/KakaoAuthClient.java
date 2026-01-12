package com.miruni.backend.global.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miruni.backend.domain.user.dto.AuthUserInfoDto;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.global.common.Constants;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Kakao OAuth 클라이언트
 * - Access Token 검증 및 사용자 정보 파싱
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoAuthClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthUserInfoDto getUserInfoByAccessToken(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    Constants.KAKAO_USERINFO_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            }

            JsonNode node = objectMapper.readTree(response.getBody());
            JsonNode accountNode = node.get("kakao_account");
            if (accountNode == null) {
                throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            }

            String email = accountNode.has("email") && !accountNode.get("email").isNull()
                    ? accountNode.get("email").asText()
                    : null;

            if (email == null) {
                throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            }

            String name = "";
            if (accountNode.has("profile") && !accountNode.get("profile").isNull()) {
                JsonNode profileNode = accountNode.get("profile");
                if (profileNode.has("nickname") && !profileNode.get("nickname").isNull()) {
                    name = profileNode.get("nickname").asText();
                }
            }

            return AuthUserInfoDto.of(email, name);

        } catch (Exception e) {
            log.warn("Kakao Access Token 검증 실패: {}", e.getMessage());
            throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }
}

