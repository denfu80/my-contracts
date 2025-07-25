package com.docmgr.api.llm;

import java.time.LocalDateTime;

/**
 * Response DTO for provider test results
 */
public class ProviderTestResult {
    
    private String providerName;
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    
    public ProviderTestResult() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ProviderTestResult(String providerName, boolean success, String message) {
        this();
        this.providerName = providerName;
        this.success = success;
        this.message = message;
    }
    
    public static ProviderTestResult success(String providerName, String message) {
        return new ProviderTestResult(providerName, true, message);
    }
    
    public static ProviderTestResult failure(String providerName, String message) {
        return new ProviderTestResult(providerName, false, message);
    }
    
    public String getProviderName() {
        return providerName;
    }
    
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}