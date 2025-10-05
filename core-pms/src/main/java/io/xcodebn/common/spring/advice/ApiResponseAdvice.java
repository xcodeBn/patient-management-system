package io.xcodebn.common.spring.advice;

import io.xcodebn.common.response.ApiResponse;
import io.xcodebn.common.util.ResponseBuilder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Automatically wraps all controller responses in ApiResponse.
 *
 * Usage:
 * 1. Enable by adding @EnableAutoConfiguration or manually import this class
 * 2. Controllers can return plain objects:
 *    return patient;  // Automatically wrapped in ApiResponse
 *
 * 3. Or manually wrap for more control:
 *    return ResponseBuilder.success(patient);
 */
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        // Only wrap if response is NOT already an ApiResponse
        return !ApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        // If body is null (204 No Content), don't wrap
        if (body == null) {
            return null;
        }

        // If it's a String (common for error responses from Spring), don't wrap to avoid casting issues
        if (body instanceof String) {
            return body;
        }

        // Wrap in success response
        return ResponseBuilder.success(body);
    }
}
