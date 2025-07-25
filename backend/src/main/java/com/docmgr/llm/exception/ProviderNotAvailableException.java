package com.docmgr.llm.exception;

/**
 * Exception thrown when a requested LLM provider is not available
 */
public class ProviderNotAvailableException extends LLMException {
    
    public ProviderNotAvailableException(String providerId) {
        super("Provider not available: " + providerId, providerId, "PROVIDER_UNAVAILABLE");
    }
    
    public ProviderNotAvailableException(String providerId, String message) {
        super(message, providerId, "PROVIDER_UNAVAILABLE");
    }
    
    public ProviderNotAvailableException(String providerId, String message, Throwable cause) {
        super(message, providerId, "PROVIDER_UNAVAILABLE", cause);
    }
}