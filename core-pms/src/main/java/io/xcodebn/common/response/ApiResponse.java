package io.xcodebn.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Generic API response wrapper for all endpoints.
 * Provides consistent response structure across all microservices.
 *
 * @param <T> The type of data being returned
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates if the request was successful
     */
    private boolean success;

    /**
     * The actual response data (null if error)
     */
    private T data;

    /**
     * Error details (null if success)
     */
    private ErrorResponse error;

    /**
     * ISO-8601 timestamp of the response
     */
    @Builder.Default
    private String timestamp = Instant.now().toString();

    /**
     * Unique request ID for tracing/debugging
     */
    private String requestId;

    /**
     * Optional metadata (pagination info, etc.)
     */
    private Map<String, Object> metadata;

    /**
     * HTTP status code
     */
    private int statusCode;

    // Static factory methods for common responses

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .statusCode(200)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String requestId) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .requestId(requestId)
                .statusCode(200)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, Map<String, Object> metadata) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .metadata(metadata)
                .statusCode(200)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorResponse error, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .statusCode(statusCode)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, int statusCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();

        return ApiResponse.<T>builder()
                .success(false)
                .error(errorResponse)
                .statusCode(statusCode)
                .timestamp(Instant.now().toString())
                .build();
    }
}