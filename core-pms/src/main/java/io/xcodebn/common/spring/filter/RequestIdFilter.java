package io.xcodebn.common.spring.filter;

import io.xcodebn.common.util.RequestContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that generates/extracts request IDs for tracking.
 * - Checks for existing X-Request-ID header
 * - Generates new one if not present
 * - Stores in RequestContext for use in responses
 * - Adds to response headers for client tracking
 *
 * Usage: Auto-registered as a Spring component
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Extract or generate request ID
            String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isEmpty()) {
                requestId = generateRequestId();
            }

            // Extract correlation ID (for distributed tracing)
            String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = requestId; // Use request ID as correlation ID if not provided
            }

            // Store in thread-local context
            RequestContext.setRequestId(requestId);
            RequestContext.setCorrelationId(correlationId);

            // Add to response headers
            httpResponse.setHeader(REQUEST_ID_HEADER, requestId);
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

            log.debug("Request ID: {}, Correlation ID: {}, Path: {}",
                    requestId, correlationId, httpRequest.getRequestURI());

            // Continue filter chain
            chain.doFilter(request, response);

        } finally {
            // CRITICAL: Clean up thread-local to prevent memory leaks
            RequestContext.clear();
        }
    }

    private String generateRequestId() {
        return "req-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
