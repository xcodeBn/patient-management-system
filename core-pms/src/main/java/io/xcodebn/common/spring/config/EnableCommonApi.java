package io.xcodebn.common.spring.config;

import io.xcodebn.common.spring.advice.ApiResponseAdvice;
import io.xcodebn.common.spring.filter.RequestIdFilter;
import io.xcodebn.common.spring.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enable common API features (auto-wrapping, exception handling, request tracking).
 *
 * Usage:
 * @SpringBootApplication
 * @EnableCommonApi
 * public class MyServiceApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyServiceApplication.class, args);
 *     }
 * }
 *
 * This automatically enables:
 * - GlobalExceptionHandler (consistent error responses)
 * - ApiResponseAdvice (auto-wrap responses)
 * - RequestIdFilter (request ID tracking)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
    GlobalExceptionHandler.class,
    ApiResponseAdvice.class,
    RequestIdFilter.class
})
public @interface EnableCommonApi {
}
