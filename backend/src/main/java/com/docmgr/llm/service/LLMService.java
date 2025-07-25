package com.docmgr.llm.service;

import com.docmgr.llm.LLMProvider;
import com.docmgr.llm.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main service interface for LLM operations with provider orchestration
 */
public interface LLMService {
    
    /**
     * Complete text using the active LLM provider
     * @param prompt the input text prompt
     * @param options completion configuration options
     * @return future containing the LLM response
     */
    CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options);
    
    /**
     * Analyze text with structured output using the active provider
     * @param text the text to analyze
     * @param schema the expected output structure
     * @return future containing structured analysis response
     */
    CompletableFuture<StructuredResponse> analyze(String text, AnalysisSchema schema);
    
    /**
     * Get the currently active LLM provider
     * @return the active provider instance
     */
    LLMProvider getActiveProvider();
    
    /**
     * Get all available LLM providers
     * @return list of available providers
     */
    List<LLMProvider> getAvailableProviders();
    
    /**
     * Switch to a different LLM provider
     * @param providerName name of the provider to activate
     * @return future that completes when switch is done
     */
    CompletableFuture<Void> switchProvider(String providerName);
    
    /**
     * Get aggregated usage statistics across all providers
     * @return combined usage statistics
     */
    UsageStats getAggregatedUsage();
    
    /**
     * Get health status for a specific provider
     * @param providerName name of the provider
     * @return health information for the provider
     */
    ProviderHealth getProviderHealth(String providerName);
    
    /**
     * Get health status for all providers
     * @return map of provider names to health status
     */
    java.util.Map<String, ProviderHealth> getAllProviderHealth();
    
    /**
     * Test connectivity to a specific provider
     * @param providerName name of the provider to test
     * @return future containing test result
     */
    CompletableFuture<Boolean> testProvider(String providerName);
    
    /**
     * Test connectivity to the active provider
     * @return future containing test result
     */
    CompletableFuture<Boolean> testActiveProvider();
}