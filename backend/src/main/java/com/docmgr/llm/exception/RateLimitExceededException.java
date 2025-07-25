package com.docmgr.llm.exception;

/**
 * Exception thrown when rate limits are exceeded for a provider
 */
public class RateLimitExceededException extends LLMException {
    
    private final int requestsPerMinute;
    private final long retryAfterSeconds;
    
    public RateLimitExceededException(String providerId, int requestsPerMinute) {
        super("Rate limit exceeded for provider: " + providerId + 
              " (limit: " + requestsPerMinute + " requests/minute)", 
              providerId, "RATE_LIMIT_EXCEEDED");
        this.requestsPerMinute = requestsPerMinute;
        this.retryAfterSeconds = 60;
    }
    
    public RateLimitExceededException(String providerId, int requestsPerMinute, long retryAfterSeconds) {
        super("Rate limit exceeded for provider: " + providerId + 
              " (limit: " + requestsPerMinute + " requests/minute). Retry after: " + retryAfterSeconds + " seconds", 
              providerId, "RATE_LIMIT_EXCEEDED");
        this.requestsPerMinute = requestsPerMinute;
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    public RateLimitExceededException(String message) {
        super(message, null, "RATE_LIMIT_EXCEEDED");
        this.requestsPerMinute = -1;
        this.retryAfterSeconds = 60;
    }
    
    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }
    
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}