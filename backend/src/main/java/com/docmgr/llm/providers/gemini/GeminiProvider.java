package com.docmgr.llm.providers.gemini;

import com.docmgr.llm.LLMProvider;
import com.docmgr.llm.config.LLMProperties;
import com.docmgr.llm.exception.LLMException;
import com.docmgr.llm.exception.ProviderNotAvailableException;
import com.docmgr.llm.model.*;
import com.docmgr.llm.ratelimit.RateLimited;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Google Gemini LLM provider implementation with rate limiting and retry logic
 */
@Component("geminiProvider")
@ConditionalOnProperty(name = "app.llm.gemini.enabled", havingValue = "true")
public class GeminiProvider implements LLMProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(GeminiProvider.class);
    private static final String PROVIDER_NAME = "gemini";
    
    private final WebClient webClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final LLMProperties properties;
    private final ObjectMapper objectMapper;
    private final UsageStats usageStats;
    
    public GeminiProvider(WebClient.Builder webClientBuilder,
                         RedisTemplate<String, String> redisTemplate,
                         LLMProperties properties,
                         ObjectMapper objectMapper) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.usageStats = new UsageStats();
        
        LLMProperties.Gemini geminiConfig = properties.getGemini();
        
        this.webClient = webClientBuilder
            .baseUrl(geminiConfig.getBaseUrl())
            .defaultHeader("Content-Type", "application/json")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
            .build();
        
        logger.info("Gemini provider initialized with base URL: {}, enabled: {}, hasApiKey: {}", 
                   geminiConfig.getBaseUrl(), geminiConfig.isEnabled(), 
                   geminiConfig.getApiKey() != null && !geminiConfig.getApiKey().trim().isEmpty());
    }
    
    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public ProviderType getType() {
        return ProviderType.GEMINI_API;
    }
    
    @Override
    public boolean isAvailable() {
        try {
            LLMProperties.Gemini config = properties.getGemini();
            return config.isEnabled() && 
                   config.getApiKey() != null && 
                   !config.getApiKey().trim().isEmpty();
        } catch (Exception e) {
            logger.warn("Error checking Gemini availability", e);
            return false;
        }
    }
    
    @Override
    @RateLimited(provider = PROVIDER_NAME, requestsPerMinute = 15)
    @Retryable(value = {WebClientResponseException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options) {
        if (!isAvailable()) {
            return CompletableFuture.failedFuture(
                new ProviderNotAvailableException(PROVIDER_NAME, "Provider not properly configured"));
        }
        
        LLMProperties.Gemini config = properties.getGemini();
        
        try {
            GeminiRequest request = buildGeminiRequest(prompt, options);
            String uri = "/v1beta/models/" + config.getModel() + ":generateContent?key=" + config.getApiKey();
            
            long startTime = System.currentTimeMillis();
            
            return webClient.post()
                .uri(uri)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    logger.error("Gemini API error: {}", response.statusCode());
                    return response.bodyToMono(String.class)
                        .map(body -> new LLMException("Gemini API error: " + body, PROVIDER_NAME, "API_ERROR"));
                })
                .bodyToMono(GeminiResponse.class)
                .map(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    return mapToLLMResponse(response, duration);
                })
                .doOnSuccess(response -> recordUsage(response))
                .doOnError(error -> logger.error("Gemini completion failed", error))
                .toFuture();
                
        } catch (Exception e) {
            logger.error("Failed to create Gemini request", e);
            return CompletableFuture.failedFuture(
                new LLMException("Failed to create request", PROVIDER_NAME, "REQUEST_ERROR", e));
        }
    }
    
    @Override
    public CompletableFuture<StructuredResponse> analyze(String text, AnalysisSchema schema) {
        String prompt = buildAnalysisPrompt(text, schema);
        CompletionOptions options = CompletionOptions.builder()
            .maxTokens(1000)
            .temperature(0.1) // Lower temperature for structured output
            .build();
            
        return complete(prompt, options)
            .thenApply(llmResponse -> {
                StructuredResponse structured = new StructuredResponse();
                structured.setRawText(llmResponse.getText());
                structured.setProviderId(llmResponse.getProviderId());
                structured.setTimestamp(llmResponse.getTimestamp());
                structured.setTokensUsed(llmResponse.getTokensUsed());
                
                // TODO: Parse JSON response into structured data
                // For now, just return the raw text
                structured.addExtractedData("raw_response", llmResponse.getText(), 1.0);
                
                return structured;
            });
    }
    
    @Override
    public UsageStats getUsageStats() {
        return usageStats;
    }
    
    @Override
    public ProviderHealth getHealth() {
        try {
            if (!isAvailable()) {
                return ProviderHealth.unhealthy("Provider not configured or API key missing");
            }
            
            // Check recent failures from Redis
            String failureKey = "health:" + PROVIDER_NAME + ":failures";
            String failures = redisTemplate.opsForValue().get(failureKey);
            int recentFailures = failures != null ? Integer.parseInt(failures) : 0;
            
            if (recentFailures > 5) {
                return ProviderHealth.degraded("Recent failures detected: " + recentFailures);
            }
            
            return ProviderHealth.healthy("Provider operational");
            
        } catch (Exception e) {
            logger.warn("Error checking Gemini health", e);
            return ProviderHealth.unknown("Health check failed: " + e.getMessage());
        }
    }
    
    @Override
    public CompletableFuture<List<String>> getSupportedModels() {
        // Gemini models are predefined
        return CompletableFuture.completedFuture(List.of(
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro-latest",
            "gemini-1.0-pro"
        ));
    }
    
    private GeminiRequest buildGeminiRequest(String prompt, CompletionOptions options) {
        GeminiRequest.Content content = new GeminiRequest.Content();
        content.setParts(List.of(new GeminiRequest.Part(prompt)));
        
        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig();
        config.setMaxOutputTokens(options.getMaxTokens());
        config.setTemperature(options.getTemperature());
        
        GeminiRequest request = new GeminiRequest();
        request.setContents(List.of(content));
        request.setGenerationConfig(config);
        
        return request;
    }
    
    private LLMResponse mapToLLMResponse(GeminiResponse geminiResponse, long durationMs) {
        String text = "";
        int tokensUsed = 0;
        
        if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
            GeminiResponse.Candidate candidate = geminiResponse.getCandidates().get(0);
            if (candidate.getContent() != null && candidate.getContent().getParts() != null) {
                text = candidate.getContent().getParts().stream()
                    .map(GeminiResponse.Part::getText)
                    .reduce("", String::concat);
            }
        }
        
        if (geminiResponse.getUsageMetadata() != null) {
            tokensUsed = geminiResponse.getUsageMetadata().getTotalTokenCount();
        }
        
        LLMResponse response = new LLMResponse(text, tokensUsed, PROVIDER_NAME);
        response.addMetadata("response_time_ms", durationMs);
        response.addMetadata("model", properties.getGemini().getModel());
        
        return response;
    }
    
    private void recordUsage(LLMResponse response) {
        usageStats.incrementRequests();
        usageStats.addTokens(response.getTokensUsed());
        
        String today = LocalDateTime.now().toLocalDate().toString();
        usageStats.addDailyUsage(today, response.getTokensUsed());
        
        // Store in Redis for persistence
        try {
            String usageKey = "usage:" + PROVIDER_NAME;
            redisTemplate.opsForHash().increment(usageKey, "total_requests", 1);
            redisTemplate.opsForHash().increment(usageKey, "total_tokens", response.getTokensUsed());
            redisTemplate.opsForHash().increment(usageKey, "daily:" + today, response.getTokensUsed());
            redisTemplate.expire(usageKey, Duration.ofDays(30));
        } catch (Exception e) {
            logger.warn("Failed to record usage stats in Redis", e);
        }
    }
    
    private String buildAnalysisPrompt(String text, AnalysisSchema schema) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following text and extract structured information.\n\n");
        prompt.append("Text to analyze:\n").append(text).append("\n\n");
        prompt.append("Please extract the following fields:\n");
        
        schema.getFields().forEach((fieldName, fieldDef) -> {
            prompt.append("- ").append(fieldName).append(" (").append(fieldDef.getType()).append(")");
            if (fieldDef.isRequired()) {
                prompt.append(" [REQUIRED]");
            }
            if (fieldDef.getDescription() != null) {
                prompt.append(": ").append(fieldDef.getDescription());
            }
            prompt.append("\n");
        });
        
        if (schema.getInstructions() != null) {
            prompt.append("\nAdditional instructions: ").append(schema.getInstructions());
        }
        
        prompt.append("\nPlease respond with valid JSON containing the extracted fields.");
        
        return prompt.toString();
    }
}