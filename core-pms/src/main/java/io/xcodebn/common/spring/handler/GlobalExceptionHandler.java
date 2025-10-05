package io.xcodebn.common.spring.handler;

import io.xcodebn.common.response.ApiResponse;
import io.xcodebn.common.response.ErrorCode;
import io.xcodebn.common.response.ErrorResponse;
import io.xcodebn.common.response.ValidationError;
import io.xcodebn.common.spring.exception.BaseException;
import io.xcodebn.common.spring.exception.BusinessException;
import io.xcodebn.common.spring.exception.ResourceNotFoundException;
import io.xcodebn.common.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for Spring Boot applications.
 * Automatically converts exceptions to consistent ApiResponse format.
 *
 * Usage: Just add this to your Spring Boot service (it's auto-detected via @RestControllerAdvice)
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle custom BaseException and subclasses
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(
            BaseException ex,
            HttpServletRequest request
    ) {
        log.warn("BaseException: {} - {}", ex.getErrorCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .path(request.getRequestURI())
                .build();

        int statusCode = determineStatusCode(ex);
        ApiResponse<Void> response = ResponseBuilder.error(error, statusCode);

        return ResponseEntity.status(statusCode).body(response);
    }

    /**
     * Handle ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .path(request.getRequestURI())
                .build();

        ApiResponse<Void> response = ResponseBuilder.error(error, 404);

        return ResponseEntity.status(404).body(response);
    }

    /**
     * Handle BusinessException (400/409)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        log.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .path(request.getRequestURI())
                .build();

        int statusCode = determineStatusCode(ex);
        ApiResponse<Void> response = ResponseBuilder.error(error, statusCode);

        return ResponseEntity.status(statusCode).body(response);
    }

    /**
     * Handle validation errors from @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn("Validation failed for request: {}", request.getRequestURI());

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message("Validation failed")
                .validationErrors(validationErrors)
                .path(request.getRequestURI())
                .build();

        ApiResponse<Void> response = ResponseBuilder.error(error, 400);

        return ResponseEntity.status(400).body(response);
    }

    /**
     * Handle IllegalArgumentException (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.BAD_REQUEST.getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        ApiResponse<Void> response = ResponseBuilder.error(error, 400);

        return ResponseEntity.status(400).body(response);
    }

    /**
     * Handle all other unexpected exceptions (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                // Only include stack trace in dev mode (you can add profile check)
                .debugInfo(ex.getMessage())
                .build();

        ApiResponse<Void> response = ResponseBuilder.error(error, 500);

        return ResponseEntity.status(500).body(response);
    }

    // Helper methods

    private ValidationError mapFieldError(FieldError fieldError) {
        return ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .constraint(fieldError.getCode())
                .build();
    }

    private int determineStatusCode(BaseException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        return switch (errorCode) {
            case BAD_REQUEST, VALIDATION_ERROR, MISSING_REQUIRED_FIELD, INVALID_FORMAT,
                 INVALID_VALUE, INVALID_PATIENT_ID, INVALID_DATE_RANGE -> 400;
            case UNAUTHORIZED, INVALID_CREDENTIALS, TOKEN_EXPIRED, TOKEN_INVALID -> 401;
            case FORBIDDEN -> 403;
            case NOT_FOUND, PATIENT_NOT_FOUND, BILLING_ACCOUNT_NOT_FOUND, USER_NOT_FOUND,
                 ANALYTICS_DATA_NOT_AVAILABLE -> 404;
            case METHOD_NOT_ALLOWED -> 405;
            case CONFLICT, PATIENT_ALREADY_EXISTS, EMAIL_ALREADY_EXISTS,
                 BILLING_ACCOUNT_EXISTS, USER_ALREADY_EXISTS -> 409;
            case PAYMENT_FAILED, INSUFFICIENT_FUNDS -> 402;
            default -> 500;
        };
    }
}
