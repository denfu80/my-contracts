package com.docmgr.llm.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Response from an LLM provider containing generated text and metadata
 */
@Schema(description = "Response from LLM provider with generated text and metadata")
public class LLMResponse {
    
    @Schema(description = "Generated text response from the LLM", example = "Quantum computing is a revolutionary technology...")
    private String text;
    
    @Schema(description = "Number of tokens used for this generation", example = "42")
    private int tokensUsed;
    
    @Schema(description = "Timestamp when the response was generated")
    private LocalDateTime timestamp;
    
    @Schema(description = "ID of the provider that generated this response", example = "gemini")
    private String providerId;
    
    @Schema(description = "Additional metadata about the response (response time, model used, etc.)")
    private Map<String, Object> metadata = new HashMap<>();
    
    public LLMResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public LLMResponse(String text, int tokensUsed, String providerId) {
        this();
        this.text = text;
        this.tokensUsed = tokensUsed;
        this.providerId = providerId;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getTokensUsed() {
        return tokensUsed;
    }
    
    public void setTokensUsed(int tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getProviderId() {
        return providerId;
    }
    
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
}