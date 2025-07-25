package com.docmgr.llm.service;

import com.docmgr.llm.LLMProvider;
import com.docmgr.llm.config.LLMProperties;
import com.docmgr.llm.exception.LLMException;
import com.docmgr.llm.exception.ProviderNotAvailableException;
import com.docmgr.llm.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main implementation of LLM service with provider orchestration and fallback logic
 */
@Service
public class LLMServiceImpl implements LLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMServiceImpl.class);
    private static final String ACTIVE_PROVIDER_KEY = "llm:active_provider";
    
    private final Map<String, LLMProvider> providers;
    private final RedisTemplate<String, String> redisTemplate;
    private final LLMProperties properties;
    
    public LLMServiceImpl(List<LLMProvider> providerList,
                         RedisTemplate<String, String> redisTemplate,
                         LLMProperties properties) {
        this.providers = providerList.stream()
            .collect(Collectors.toMap(LLMProvider::getName, Function.identity()));
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        
        logger.info("LLM Service initialized with {} providers: {}", 
                   providers.size(), providers.keySet());
        
        // Initialize active provider if not set
        initializeActiveProvider();
    }
    
    @Override
    public CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options) {
        LLMProvider activeProvider = getActiveProvider();
        
        logger.debug("Using provider {} for completion request", activeProvider.getName());
        
        return activeProvider.complete(prompt, options)
            .handle((response, throwable) -> {
                if (throwable != null) {
                    logger.warn("Provider {} failed, attempting fallback", activeProvider.getName(), throwable);
                    return handleProviderFailure(activeProvider, prompt, options, throwable);
                }
                return CompletableFuture.completedFuture(response);
            })
            .thenCompose(Function.identity());
    }
    
    @Override
    public CompletableFuture<StructuredResponse> analyze(String text, AnalysisSchema schema) {
        LLMProvider activeProvider = getActiveProvider();
        
        logger.debug("Using provider {} for analysis request", activeProvider.getName());
        
        return activeProvider.analyze(text, schema)
            .handle((response, throwable) -> {
                if (throwable != null) {
                    logger.warn("Provider {} failed during analysis, attempting fallback", 
                              activeProvider.getName(), throwable);
                    // For analysis, we could fall back to a simple completion
                    return handleAnalysisFailure(activeProvider, text, schema, throwable);
                }
                return CompletableFuture.completedFuture(response);
            })
            .thenCompose(Function.identity());
    }
    
    @Override
    public LLMProvider getActiveProvider() {
        String activeProviderName = redisTemplate.opsForValue().get(ACTIVE_PROVIDER_KEY);
        
        if (activeProviderName == null) {
            activeProviderName = properties.getDefaultProvider();
        }
        
        LLMProvider provider = providers.get(activeProviderName);
        
        // If the configured provider is not available, find the first available one
        if (provider == null || !provider.isAvailable()) {
            provider = getFirstAvailableProvider();
            if (provider != null) {
                logger.info("Switching to available provider: {}", provider.getName());
                setActiveProvider(provider.getName());
            }
        }
        
        if (provider == null) {
            throw new ProviderNotAvailableException("NO_PROVIDER", "No LLM providers are available");
        }
        
        return provider;
    }
    
    @Override
    public List<LLMProvider> getAvailableProviders() {
        return providers.values().stream()
            .filter(LLMProvider::isAvailable)
            .collect(Collectors.toList());
    }
    
    @Override
    public CompletableFuture<Void> switchProvider(String providerName) {
        return CompletableFuture.supplyAsync(() -> {
            LLMProvider provider = providers.get(providerName);
            
            if (provider == null) {
                throw new ProviderNotAvailableException(providerName, "Provider not found: " + providerName);
            }
            
            if (!provider.isAvailable()) {
                throw new ProviderNotAvailableException(providerName, "Provider not available: " + providerName);
            }
            
            setActiveProvider(providerName);
            logger.info("Switched active provider to: {}", providerName);
            
            return null;
        });
    }
    
    @Override
    public UsageStats getAggregatedUsage() {
        UsageStats aggregated = new UsageStats();
        
        for (LLMProvider provider : providers.values()) {
            UsageStats providerStats = provider.getUsageStats();
            aggregated.setTotalRequests(aggregated.getTotalRequests() + providerStats.getTotalRequests());
            aggregated.setTotalTokens(aggregated.getTotalTokens() + providerStats.getTotalTokens());
            
            if (aggregated.getLastRequest() == null || 
                (providerStats.getLastRequest() != null && 
                 providerStats.getLastRequest().isAfter(aggregated.getLastRequest()))) {
                aggregated.setLastRequest(providerStats.getLastRequest());
            }
            
            // Merge daily usage
            providerStats.getDailyUsage().forEach((date, tokens) -> 
                aggregated.getDailyUsage().merge(date, tokens, Integer::sum));
                
            // Merge model usage
            providerStats.getModelUsage().forEach((model, tokens) -> 
                aggregated.getModelUsage().merge(model, tokens, Integer::sum));
        }
        
        return aggregated;
    }
    
    @Override
    public ProviderHealth getProviderHealth(String providerName) {
        LLMProvider provider = providers.get(providerName);
        
        if (provider == null) {
            return ProviderHealth.unknown("Provider not found: " + providerName);
        }
        
        return provider.getHealth();
    }
    
    @Override
    public Map<String, ProviderHealth> getAllProviderHealth() {
        Map<String, ProviderHealth> healthMap = new HashMap<>();
        
        for (LLMProvider provider : providers.values()) {
            healthMap.put(provider.getName(), provider.getHealth());
        }
        
        return healthMap;
    }
    
    @Override
    public CompletableFuture<Boolean> testProvider(String providerName) {
        LLMProvider provider = providers.get(providerName);
        
        if (provider == null) {
            return CompletableFuture.completedFuture(false);
        }
        
        return testProviderConnectivity(provider);
    }
    
    @Override
    public CompletableFuture<Boolean> testActiveProvider() {
        try {
            LLMProvider provider = getActiveProvider();
            return testProviderConnectivity(provider);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(false);
        }
    }
    
    private CompletableFuture<Boolean> testProviderConnectivity(LLMProvider provider) {
        CompletionOptions testOptions = CompletionOptions.builder()
            .maxTokens(10)
            .temperature(0.1)
            .build();
            
        return provider.complete("Test", testOptions)
            .thenApply(response -> response != null && response.getText() != null)
            .exceptionally(throwable -> {
                logger.debug("Provider test failed for {}: {}", provider.getName(), throwable.getMessage());
                return false;
            });
    }
    
    private CompletableFuture<LLMResponse> handleProviderFailure(
            LLMProvider failedProvider, String prompt, CompletionOptions options, Throwable error) {
        
        if (!properties.isFallbackEnabled()) {
            return CompletableFuture.failedFuture(error);
        }
        
        // Try to find a fallback provider
        String fallbackProviderName = getFallbackProvider(failedProvider.getName());
        
        if (fallbackProviderName != null) {
            LLMProvider fallbackProvider = providers.get(fallbackProviderName);
            logger.info("Attempting fallback to provider: {}", fallbackProviderName);
            
            return fallbackProvider.complete(prompt, options)
                .exceptionally(fallbackError -> {
                    logger.error("Fallback provider also failed", fallbackError);
                    throw new LLMException("All providers failed", "FALLBACK_FAILED", "ALL_PROVIDERS_FAILED");
                });
        }
        
        return CompletableFuture.failedFuture(error);
    }
    
    private CompletableFuture<StructuredResponse> handleAnalysisFailure(
            LLMProvider failedProvider, String text, AnalysisSchema schema, Throwable error) {
        
        if (!properties.isFallbackEnabled()) {
            return CompletableFuture.failedFuture(error);
        }
        
        String fallbackProviderName = getFallbackProvider(failedProvider.getName());
        
        if (fallbackProviderName != null) {
            LLMProvider fallbackProvider = providers.get(fallbackProviderName);
            logger.info("Attempting analysis fallback to provider: {}", fallbackProviderName);
            
            return fallbackProvider.analyze(text, schema)
                .exceptionally(fallbackError -> {
                    logger.error("Analysis fallback provider also failed", fallbackError);
                    throw new LLMException("All providers failed for analysis", "ANALYSIS_FAILED", "ALL_PROVIDERS_FAILED");
                });
        }
        
        return CompletableFuture.failedFuture(error);
    }
    
    private String getFallbackProvider(String failedProviderName) {
        // Simple fallback logic: if Gemini fails, use Ollama; if Ollama fails, use Gemini
        return switch (failedProviderName) {
            case "gemini" -> providers.containsKey("ollama") && providers.get("ollama").isAvailable() ? "ollama" : null;
            case "ollama" -> providers.containsKey("gemini") && providers.get("gemini").isAvailable() ? "gemini" : null;
            default -> getFirstAvailableProvider() != null ? getFirstAvailableProvider().getName() : null;
        };
    }
    
    private LLMProvider getFirstAvailableProvider() {
        return providers.values().stream()
            .filter(LLMProvider::isAvailable)
            .findFirst()
            .orElse(null);
    }
    
    private void setActiveProvider(String providerName) {
        try {
            redisTemplate.opsForValue().set(ACTIVE_PROVIDER_KEY, providerName, Duration.ofDays(7));
        } catch (Exception e) {
            logger.warn("Failed to set active provider in Redis", e);
        }
    }
    
    private void initializeActiveProvider() {
        try {
            String currentActive = redisTemplate.opsForValue().get(ACTIVE_PROVIDER_KEY);
            
            if (currentActive == null) {
                // Set default provider
                String defaultProvider = properties.getDefaultProvider();
                LLMProvider provider = providers.get(defaultProvider);
                
                if (provider != null && provider.isAvailable()) {
                    setActiveProvider(defaultProvider);
                    logger.info("Initialized active provider to default: {}", defaultProvider);
                } else {
                    // Find first available provider
                    LLMProvider firstAvailable = getFirstAvailableProvider();
                    if (firstAvailable != null) {
                        setActiveProvider(firstAvailable.getName());
                        logger.info("Initialized active provider to first available: {}", firstAvailable.getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to initialize active provider", e);
        }
    }
}