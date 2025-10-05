package io.xcodebn.common.response;

/**
 * Standard error codes used across all microservices.
 * Provides consistent error identification for frontend.
 */
public enum ErrorCode {

    // General errors (1xxx)
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred"),
    BAD_REQUEST("BAD_REQUEST", "Invalid request"),
    UNAUTHORIZED("UNAUTHORIZED", "Authentication required"),
    FORBIDDEN("FORBIDDEN", "Access denied"),
    NOT_FOUND("NOT_FOUND", "Resource not found"),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "HTTP method not allowed"),
    CONFLICT("CONFLICT", "Resource conflict"),

    // Validation errors (2xxx)
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation failed"),
    MISSING_REQUIRED_FIELD("MISSING_REQUIRED_FIELD", "Required field is missing"),
    INVALID_FORMAT("INVALID_FORMAT", "Invalid field format"),
    INVALID_VALUE("INVALID_VALUE", "Invalid field value"),

    // Patient service errors (3xxx)
    PATIENT_NOT_FOUND("PATIENT_NOT_FOUND", "Patient not found"),
    PATIENT_ALREADY_EXISTS("PATIENT_ALREADY_EXISTS", "Patient already exists"),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email address already exists"),
    INVALID_PATIENT_ID("INVALID_PATIENT_ID", "Invalid patient ID"),

    // Billing service errors (4xxx)
    BILLING_ACCOUNT_NOT_FOUND("BILLING_ACCOUNT_NOT_FOUND", "Billing account not found"),
    BILLING_ACCOUNT_EXISTS("BILLING_ACCOUNT_EXISTS", "Billing account already exists"),
    PAYMENT_FAILED("PAYMENT_FAILED", "Payment processing failed"),
    INSUFFICIENT_FUNDS("INSUFFICIENT_FUNDS", "Insufficient funds"),

    // Auth service errors (5xxx)
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid username or password"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Authentication token has expired"),
    TOKEN_INVALID("TOKEN_INVALID", "Invalid authentication token"),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "User already exists"),

    // Analytics service errors (6xxx)
    ANALYTICS_DATA_NOT_AVAILABLE("ANALYTICS_DATA_NOT_AVAILABLE", "Analytics data not available"),
    INVALID_DATE_RANGE("INVALID_DATE_RANGE", "Invalid date range"),

    // External service errors (7xxx)
    EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", "External service error"),
    GRPC_CONNECTION_ERROR("GRPC_CONNECTION_ERROR", "gRPC connection error"),
    KAFKA_ERROR("KAFKA_ERROR", "Kafka messaging error"),
    DATABASE_ERROR("DATABASE_ERROR", "Database error");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.builder()
                .code(this.code)
                .message(this.defaultMessage)
                .build();
    }

    public ErrorResponse toErrorResponse(String customMessage) {
        return ErrorResponse.builder()
                .code(this.code)
                .message(customMessage)
                .build();
    }
}
