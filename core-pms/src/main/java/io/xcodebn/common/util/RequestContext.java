package io.xcodebn.common.util;

/**
 * Thread-local storage for request-scoped data.
 * Useful for passing request ID, user context, etc. through the call stack.
 */
public class RequestContext {

    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    private RequestContext() {
        // Utility class
    }

    public static void setRequestId(String requestId) {
        REQUEST_ID.set(requestId);
    }

    public static String getRequestId() {
        return REQUEST_ID.get();
    }

    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }

    public static String getUserId() {
        return USER_ID.get();
    }

    public static void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    public static String getCorrelationId() {
        return CORRELATION_ID.get();
    }

    /**
     * Clear all thread-local data. MUST be called at the end of request processing.
     */
    public static void clear() {
        REQUEST_ID.remove();
        USER_ID.remove();
        CORRELATION_ID.remove();
    }
}
