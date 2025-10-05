package io.xcodebn.common.spring.exception;

import io.xcodebn.common.response.ErrorCode;
import lombok.Getter;

import java.util.Map;

/**
 * Base exception for all custom application exceptions.
 * Carries error code and additional details.
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    protected BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    protected BaseException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    protected BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = null;
    }

    protected BaseException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }
}
