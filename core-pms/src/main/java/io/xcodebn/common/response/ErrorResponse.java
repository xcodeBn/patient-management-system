package io.xcodebn.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Structured error response for API errors.
 * Provides consistent error format across all microservices.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Machine-readable error code (e.g., "PATIENT_NOT_FOUND", "VALIDATION_ERROR")
     */
    private String code;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Additional error details (field errors, nested errors, etc.)
     */
    private Map<String, Object> details;

    /**
     * List of validation errors (for validation failures)
     */
    private List<ValidationError> validationErrors;

    /**
     * Stack trace or debugging info (only in dev mode)
     */
    private String debugInfo;

    /**
     * Path where the error occurred
     */
    private String path;

    // Factory methods

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ErrorResponse of(String code, String message, Map<String, Object> details) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
    }

    public static ErrorResponse validation(List<ValidationError> validationErrors) {
        return ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .validationErrors(validationErrors)
                .build();
    }
}
