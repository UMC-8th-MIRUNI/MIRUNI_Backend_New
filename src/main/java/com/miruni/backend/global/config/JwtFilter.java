package com.miruni.backend.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.miruni.backend.global.authroize.CustomUserDetailService;
import com.miruni.backend.global.authroize.TokenService;
import com.miruni.backend.global.common.JwtUtil;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String token = jwtUtil.resolveToken(request);
        
        if (token != null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    
                    // 블랙리스트 체크
                    if (tokenService.isTokenBlacklisted(token)) {
                        log.warn("블랙리스트된 토큰 사용 시도");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    
                    // 임시 토큰인지 확인
                    String tokenType = jwtUtil.getTokenType(token);
                    if ("temp".equals(tokenType)) {
                        log.debug("임시 토큰 사용: userId={}", userId);
                    }
                    
                    UserDetails userDetails = customUserDetailService.loadUserById(userId);
                    
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("JWT 토큰 검증 성공: userId={}", userId);
                }
            } catch (Exception e) {
                log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}

