package com.miruni.backend.global.authroize;

import com.miruni.backend.global.exception.BaseException;
import com.miruni.backend.global.exception.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @LoginUser 어노테이션이 붙은 파라미터에 현재 인증된 사용자 ID를 자동으로 주입
 */
@Slf4j
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @LoginUser 어노테이션이 있고, Long 타입인 경우 지원
        return parameter.hasParameterAnnotation(LoginUser.class) 
                && Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("인증되지 않은 사용자의 @LoginUser 접근 시도");
            throw BaseException.type(CommonErrorCode.UNAUTHORIZED);
        }
        
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            Long userId = userDetails.getId();
            log.debug("@LoginUser 파라미터 주입: userId={}", userId);
            return userId;
        }
        
        log.error("CustomUserDetails 타입이 아닌 Principal: {}", authentication.getPrincipal().getClass());
        throw BaseException.type(CommonErrorCode.UNAUTHORIZED);
    }
}

