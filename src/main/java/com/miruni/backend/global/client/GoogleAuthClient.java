package com.miruni.backend.global.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miruni.backend.domain.user.dto.AuthUserInfoDto;
import com.miruni.backend.domain.user.exception.UserErrorCode;
import com.miruni.backend.global.common.Constants;
import com.miruni.backend.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Google OAuth 클라이언트
 * - ID Token 검증 및 사용자 정보 파싱
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleAuthClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthUserInfoDto getUserInfoByIdToken(String idToken) {
        try {
            String url = Constants.GOOGLE_TOKENINFO_URL + idToken;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            }

            JsonNode node = objectMapper.readTree(response.getBody());

            String email = node.get("email").asText();
            String name = node.has("name") ? node.get("name").asText() : "";

            return AuthUserInfoDto.of(email, name);

        } catch (Exception e) {
            log.warn("Google ID 토큰 검증 실패: {}", e.getMessage());
            throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }
}

