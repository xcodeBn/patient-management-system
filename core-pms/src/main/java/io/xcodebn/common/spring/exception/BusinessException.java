package io.xcodebn.common.spring.exception;

import io.xcodebn.common.response.ErrorCode;

import java.util.Map;

/**
 * Exception for business logic violations (400/409).
 * Use for validation failures, conflicts, etc.
 */
public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
