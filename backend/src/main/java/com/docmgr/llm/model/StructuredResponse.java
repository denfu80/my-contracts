package com.docmgr.llm.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Response from structured analysis with extracted data and confidence scores
 */
public class StructuredResponse {
    
    private Map<String, Object> data = new HashMap<>();
    private Map<String, Double> confidenceScores = new HashMap<>();
    private String rawText;
    private String providerId;
    private LocalDateTime timestamp;
    private int tokensUsed;
    
    public StructuredResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public StructuredResponse(Map<String, Object> data, String providerId) {
        this();
        this.data = data;
        this.providerId = providerId;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public Map<String, Double> getConfidenceScores() {
        return confidenceScores;
    }
    
    public void setConfidenceScores(Map<String, Double> confidenceScores) {
        this.confidenceScores = confidenceScores;
    }
    
    public String getRawText() {
        return rawText;
    }
    
    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
    
    public String getProviderId() {
        return providerId;
    }
    
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getTokensUsed() {
        return tokensUsed;
    }
    
    public void setTokensUsed(int tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
    
    public void addExtractedData(String key, Object value, Double confidence) {
        this.data.put(key, value);
        if (confidence != null) {
            this.confidenceScores.put(key, confidence);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getExtractedValue(String key, Class<T> type) {
        Object value = data.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    public Double getConfidenceScore(String key) {
        return confidenceScores.get(key);
    }
}