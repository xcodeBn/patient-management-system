package io.xcodebn.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single validation error for a field.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationError {

    /**
     * The field that failed validation
     */
    private String field;

    /**
     * The error message
     */
    private String message;

    /**
     * The rejected value (optional)
     */
    private Object rejectedValue;

    /**
     * The validation constraint that failed (e.g., "NotNull", "Email")
     */
    private String constraint;

    public static ValidationError of(String field, String message) {
        return ValidationError.builder()
                .field(field)
                .message(message)
                .build();
    }

    public static ValidationError of(String field, String message, Object rejectedValue) {
        return ValidationError.builder()
                .field(field)
                .message(message)
                .rejectedValue(rejectedValue)
                .build();
    }
}
