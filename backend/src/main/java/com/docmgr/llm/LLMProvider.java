package com.docmgr.llm;

import com.docmgr.llm.model.*;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract interface for Large Language Model providers.
 * Supports multiple implementations (Gemini, Ollama, etc.) with unified API.
 */
public interface LLMProvider {
    
    /**
     * Get the unique name of this provider
     * @return provider name (e.g., "gemini", "ollama")
     */
    String getName();
    
    /**
     * Get the type of this provider
     * @return provider type enum
     */
    ProviderType getType();
    
    /**
     * Check if the provider is currently available
     * @return true if provider can handle requests
     */
    boolean isAvailable();
    
    /**
     * Generate text completion for the given prompt
     * @param prompt the input text prompt
     * @param options completion configuration options
     * @return future containing the LLM response
     */
    CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options);
    
    /**
     * Analyze text with structured output schema
     * @param text the text to analyze
     * @param schema the expected output structure
     * @return future containing structured analysis response
     */
    CompletableFuture<StructuredResponse> analyze(String text, AnalysisSchema schema);
    
    /**
     * Get current usage statistics for this provider
     * @return usage statistics
     */
    UsageStats getUsageStats();
    
    /**
     * Get current health status of this provider
     * @return health information
     */
    ProviderHealth getHealth();
    
    /**
     * Get supported models for this provider
     * @return future containing list of available model names
     */
    CompletableFuture<java.util.List<String>> getSupportedModels();
}