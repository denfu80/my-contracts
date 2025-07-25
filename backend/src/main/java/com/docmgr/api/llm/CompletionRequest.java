package com.docmgr.api.llm;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for text completion API
 */
public class CompletionRequest {
    
    @NotBlank(message = "Prompt cannot be empty")
    @Size(max = 50000, message = "Prompt cannot exceed 50,000 characters")
    private String prompt;
    
    @Min(value = 1, message = "Max tokens must be at least 1")
    @Max(value = 4000, message = "Max tokens cannot exceed 4000")
    private Integer maxTokens;
    
    @Min(value = 0, message = "Temperature must be at least 0")
    @Max(value = 2, message = "Temperature cannot exceed 2")
    private Double temperature;
    
    private String model;
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public Integer getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
}