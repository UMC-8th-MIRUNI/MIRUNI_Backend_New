package com.miruni.backend.global.authroize;

import com.miruni.backend.global.common.JwtUtil;
import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @AuthToken 어노테이션이 붙은 파라미터에 액세스 토큰을 자동으로 주입
 * Authorization 헤더에서 "Bearer " 제거한 순수 토큰 반환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @AuthToken 어노테이션이 있고, String 타입인 경우 지원
        return parameter.hasParameterAnnotation(AuthToken.class) 
                && String.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        
        String accessToken = jwtUtil.resolveToken(request);
        
        if (accessToken == null) {
            log.warn("Authorization 헤더가 없거나 형식이 잘못됨");
            throw BaseException.type(CommonErrorCode.UNAUTHORIZED);
        }
        
        log.debug("@AuthToken 파라미터 주입: token={}", accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
        return accessToken;
    }
}

