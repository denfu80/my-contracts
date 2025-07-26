package com.docmgr.api.llm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for text completion API
 */
@Schema(description = "Request for text completion using LLM providers")
public class CompletionRequest {
    
    @Schema(description = "The input text prompt for the LLM", example = "Explain quantum computing in simple terms", required = true)
    @NotBlank(message = "Prompt cannot be empty")
    @Size(max = 50000, message = "Prompt cannot exceed 50,000 characters")
    private String prompt;
    
    @Schema(description = "Maximum number of tokens to generate", example = "100", minimum = "1", maximum = "4000")
    @Min(value = 1, message = "Max tokens must be at least 1")
    @Max(value = 4000, message = "Max tokens cannot exceed 4000")
    private Integer maxTokens;
    
    @Schema(description = "Randomness of the output (0.0 = deterministic, 2.0 = very random)", example = "0.7", minimum = "0", maximum = "2")
    @Min(value = 0, message = "Temperature must be at least 0")
    @Max(value = 2, message = "Temperature cannot exceed 2")
    private Double temperature;
    
    @Schema(description = "Specific model to use (optional, uses provider default if not specified)", example = "gemini-1.5-flash-latest")
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