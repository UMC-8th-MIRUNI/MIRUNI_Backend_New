package com.miruni.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miruni.backend.global.exception.CommonErrorCode;
import com.miruni.backend.global.exception.CustomErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                //CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // session 사용 X
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // form login 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                //http Basic 인증 비활성화 - jwt 구현 전 임시 활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // JWT 필터 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                //URL별 권한 설정
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**",
                                "/api/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // 인증/인가 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                );
                // .httpBasic(Customizer.withDefaults()); //임시로 httpBasic 활성화

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 실패 시 처리 (401 Unauthorized)
     * 인증되지 않은 사용자가 보호된 리소스에 접근할 때
     */
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn("인증 실패: {}", authException.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, CommonErrorCode.UNAUTHORIZED);
        };
    }

    /**
     * 인가 실패 시 처리 (403 Forbidden)
     * 인증은 되었지만 권한이 없을 때
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.warn("접근 거부: {}", accessDeniedException.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, CommonErrorCode.FORBIDDEN);
        };
    }

    /**
     * 공통 에러 응답 설정
     */
    private void setErrorResponse(HttpServletResponse response, int status, CommonErrorCode errorCode) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CustomErrorResponse errorResponse = CustomErrorResponse.from(errorCode);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
