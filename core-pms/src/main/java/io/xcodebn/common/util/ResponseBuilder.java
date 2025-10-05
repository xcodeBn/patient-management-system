package io.xcodebn.common.util;

import io.xcodebn.common.response.ApiResponse;
import io.xcodebn.common.response.ErrorCode;
import io.xcodebn.common.response.ErrorResponse;
import io.xcodebn.common.response.PagedResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for building consistent API responses.
 * Provides fluent API for creating responses.
 */
public class ResponseBuilder {

    private ResponseBuilder() {
        // Utility class
    }

    // Success responses

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .statusCode(200)
                .requestId(generateRequestId())
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .statusCode(201)
                .requestId(generateRequestId())
                .build();
    }

    public static <T> ApiResponse<T> noContent() {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(204)
                .requestId(generateRequestId())
                .build();
    }

    public static <T> ApiResponse<T> successWithMetadata(T data, Map<String, Object> metadata) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .metadata(metadata)
                .statusCode(200)
                .requestId(generateRequestId())
                .build();
    }

    // Paginated response

    public static <T> ApiResponse<PagedResponse<T>> page(List<T> content, int page, int size, long totalElements) {
        PagedResponse<T> pagedData = PagedResponse.of(content, page, size, totalElements);
        return success(pagedData);
    }

    // Error responses

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(errorCode.toErrorResponse())
                .statusCode(determineHttpStatus(errorCode))
                .requestId(generateRequestId())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(errorCode.toErrorResponse(customMessage))
                .statusCode(determineHttpStatus(errorCode))
                .requestId(generateRequestId())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorResponse errorResponse, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(errorResponse)
                .statusCode(statusCode)
                .requestId(generateRequestId())
                .build();
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(ErrorCode.BAD_REQUEST, message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(ErrorCode.NOT_FOUND, message);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(ErrorCode.UNAUTHORIZED, message);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return error(ErrorCode.FORBIDDEN, message);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return error(ErrorCode.CONFLICT, message);
    }

    public static <T> ApiResponse<T> internalError(String message) {
        return error(ErrorCode.INTERNAL_SERVER_ERROR, message);
    }

    // Error with details

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage, Map<String, Object> details) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(customMessage)
                .details(details)
                .build();

        return ApiResponse.<T>builder()
                .success(false)
                .error(errorResponse)
                .statusCode(determineHttpStatus(errorCode))
                .requestId(generateRequestId())
                .build();
    }

    public static <T> ApiResponse<T> errorWithPath(ErrorCode errorCode, String message, String path) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .path(path)
                .build();

        return ApiResponse.<T>builder()
                .success(false)
                .error(errorResponse)
                .statusCode(determineHttpStatus(errorCode))
                .requestId(generateRequestId())
                .build();
    }

    // Validation errors

    public static <T> ApiResponse<T> validationError(List<io.xcodebn.common.response.ValidationError> validationErrors) {
        ErrorResponse errorResponse = ErrorResponse.validation(validationErrors);

        return ApiResponse.<T>builder()
                .success(false)
                .error(errorResponse)
                .statusCode(400)
                .requestId(generateRequestId())
                .build();
    }

    public static <T> ApiResponse<T> validationError(String field, String message) {
        io.xcodebn.common.response.ValidationError validationError =
            io.xcodebn.common.response.ValidationError.of(field, message);

        return validationError(List.of(validationError));
    }

    // Helpers

    private static String generateRequestId() {
        // Use request ID from context if available, otherwise generate new one
        String contextRequestId = RequestContext.getRequestId();
        if (contextRequestId != null && !contextRequestId.isEmpty()) {
            return contextRequestId;
        }
        return "req-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static int determineHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case BAD_REQUEST, VALIDATION_ERROR, MISSING_REQUIRED_FIELD, INVALID_FORMAT, INVALID_VALUE,
                 INVALID_PATIENT_ID, INVALID_DATE_RANGE -> 400;
            case UNAUTHORIZED, INVALID_CREDENTIALS, TOKEN_EXPIRED, TOKEN_INVALID -> 401;
            case FORBIDDEN -> 403;
            case NOT_FOUND, PATIENT_NOT_FOUND, BILLING_ACCOUNT_NOT_FOUND, USER_NOT_FOUND,
                 ANALYTICS_DATA_NOT_AVAILABLE -> 404;
            case METHOD_NOT_ALLOWED -> 405;
            case CONFLICT, PATIENT_ALREADY_EXISTS, EMAIL_ALREADY_EXISTS, BILLING_ACCOUNT_EXISTS,
                 USER_ALREADY_EXISTS -> 409;
            case PAYMENT_FAILED, INSUFFICIENT_FUNDS -> 402;
            default -> 500;
        };
    }
}
