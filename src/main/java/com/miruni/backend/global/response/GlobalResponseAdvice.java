package com.miruni.backend.global.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();

        //제외 타입
        return !parameterType.equals(ApiResponse.class) &&      // 이미 래핑됨
                !ResponseEntity.class.isAssignableFrom(parameterType) &&  // ResponseEntity와 그 하위 타입들
                !parameterType.equals(String.class) &&           // Swagger UI 관련
                !parameterType.getName().startsWith("org.springframework"); // Spring 내부 클래스들
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        String path = request.getURI().getPath();

        if(path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger") ||
                path.startsWith("/error")){
            return body;
        }

        return ApiResponse.success(body);



    }
}

// 지금 ResponseEntity<CustomErrorResponse>는 래핑되지 않는 것 같은데 실패 응답도 성공이랑 동일하게 가져갈까?
