package com.docmgr.llm.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health status information for an LLM provider
 */
public class ProviderHealth {
    
    public enum HealthStatus {
        HEALTHY,
        DEGRADED,
        UNHEALTHY,
        UNKNOWN
    }
    
    private HealthStatus status = HealthStatus.UNKNOWN;
    private String message;
    private LocalDateTime lastChecked;
    private long responseTimeMs = -1;
    private Map<String, Object> details = new HashMap<>();
    
    public ProviderHealth() {
        this.lastChecked = LocalDateTime.now();
    }
    
    public ProviderHealth(HealthStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
    
    public static ProviderHealth healthy() {
        return new ProviderHealth(HealthStatus.HEALTHY, "Provider is operational");
    }
    
    public static ProviderHealth healthy(String message) {
        return new ProviderHealth(HealthStatus.HEALTHY, message);
    }
    
    public static ProviderHealth degraded(String message) {
        return new ProviderHealth(HealthStatus.DEGRADED, message);
    }
    
    public static ProviderHealth unhealthy(String message) {
        return new ProviderHealth(HealthStatus.UNHEALTHY, message);
    }
    
    public static ProviderHealth unknown(String message) {
        return new ProviderHealth(HealthStatus.UNKNOWN, message);
    }
    
    public HealthStatus getStatus() {
        return status;
    }
    
    public void setStatus(HealthStatus status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getLastChecked() {
        return lastChecked;
    }
    
    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }
    
    public long getResponseTimeMs() {
        return responseTimeMs;
    }
    
    public void setResponseTimeMs(long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
    
    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
    
    public boolean isHealthy() {
        return status == HealthStatus.HEALTHY;
    }
    
    public boolean isAvailable() {
        return status == HealthStatus.HEALTHY || status == HealthStatus.DEGRADED;
    }
}