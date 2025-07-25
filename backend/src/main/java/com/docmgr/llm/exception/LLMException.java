package com.docmgr.llm.exception;

/**
 * Base exception for LLM service errors
 */
public class LLMException extends RuntimeException {
    
    private final String providerId;
    private final String errorCode;
    
    public LLMException(String message) {
        super(message);
        this.providerId = null;
        this.errorCode = null;
    }
    
    public LLMException(String message, Throwable cause) {
        super(message, cause);
        this.providerId = null;
        this.errorCode = null;
    }
    
    public LLMException(String message, String providerId, String errorCode) {
        super(message);
        this.providerId = providerId;
        this.errorCode = errorCode;
    }
    
    public LLMException(String message, String providerId, String errorCode, Throwable cause) {
        super(message, cause);
        this.providerId = providerId;
        this.errorCode = errorCode;
    }
    
    public String getProviderId() {
        return providerId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}