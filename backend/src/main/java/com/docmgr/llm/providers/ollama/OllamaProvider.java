package com.docmgr.llm.providers.ollama;

import com.docmgr.llm.LLMProvider;
import com.docmgr.llm.config.LLMProperties;
import com.docmgr.llm.exception.LLMException;
import com.docmgr.llm.exception.ProviderNotAvailableException;
import com.docmgr.llm.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Ollama LLM provider implementation for local model execution
 */
@Component("ollamaProvider")
@ConditionalOnProperty(name = "app.llm.ollama.enabled", havingValue = "true", matchIfMissing = true)
public class OllamaProvider implements LLMProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(OllamaProvider.class);
    private static final String PROVIDER_NAME = "ollama";
    
    private final WebClient webClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final LLMProperties properties;
    private final ObjectMapper objectMapper;
    private final UsageStats usageStats;
    
    public OllamaProvider(WebClient.Builder webClientBuilder,
                         RedisTemplate<String, String> redisTemplate,
                         LLMProperties properties,
                         ObjectMapper objectMapper) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.usageStats = new UsageStats();
        
        LLMProperties.Ollama ollamaConfig = properties.getOllama();
        
        this.webClient = webClientBuilder
            .baseUrl(ollamaConfig.getBaseUrl())
            .defaultHeader("Content-Type", "application/json")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)) // 5MB for larger responses
            .build();
        
        logger.info("Ollama provider initialized with base URL: {}", ollamaConfig.getBaseUrl());
    }
    
    @Override
    public String getName() {
        return PROVIDER_NAME;
    }
    
    @Override
    public ProviderType getType() {
        return ProviderType.OLLAMA_LOCAL;
    }
    
    @Override
    public boolean isAvailable() {
        try {
            LLMProperties.Ollama config = properties.getOllama();
            if (!config.isEnabled()) {
                return false;
            }
            
            // Test connectivity to Ollama
            return testConnectivity().join();
        } catch (Exception e) {
            logger.warn("Error checking Ollama availability", e);
            return false;
        }
    }
    
    @Override
    public CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options) {
        if (!isAvailable()) {
            return CompletableFuture.failedFuture(
                new ProviderNotAvailableException(PROVIDER_NAME, "Ollama service not available"));
        }
        
        LLMProperties.Ollama config = properties.getOllama();
        String model = options.getModel() != null ? options.getModel() : config.getDefaultModel();
        
        try {
            OllamaRequest request = buildOllamaRequest(prompt, options, model);
            
            long startTime = System.currentTimeMillis();
            
            return webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    logger.error("Ollama API error: {}", response.statusCode());
                    return response.bodyToMono(String.class)
                        .map(body -> new LLMException("Ollama API error: " + body, PROVIDER_NAME, "API_ERROR"));
                })
                .bodyToMono(OllamaResponse.class)
                .map(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    return mapToLLMResponse(response, duration, model);
                })
                .doOnSuccess(response -> recordUsage(response))
                .doOnError(error -> {
                    logger.error("Ollama completion failed", error);
                    recordFailure();
                })
                .toFuture();
                
        } catch (Exception e) {
            logger.error("Failed to create Ollama request", e);
            return CompletableFuture.failedFuture(
                new LLMException("Failed to create request", PROVIDER_NAME, "REQUEST_ERROR", e));
        }
    }
    
    @Override
    public CompletableFuture<StructuredResponse> analyze(String text, AnalysisSchema schema) {
        String prompt = buildAnalysisPrompt(text, schema);
        CompletionOptions options = CompletionOptions.builder()
            .maxTokens(1500)
            .temperature(0.2) // Lower temperature for structured output
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
            if (!properties.getOllama().isEnabled()) {
                return ProviderHealth.unhealthy("Provider disabled in configuration");
            }
            
            long startTime = System.currentTimeMillis();
            boolean isConnected = testConnectivity().join();
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (isConnected) {
                ProviderHealth health = ProviderHealth.healthy("Ollama service operational");
                health.setResponseTimeMs(responseTime);
                
                // Check for recent failures
                String failureKey = "health:" + PROVIDER_NAME + ":failures";
                String failures = redisTemplate.opsForValue().get(failureKey);
                int recentFailures = failures != null ? Integer.parseInt(failures) : 0;
                
                if (recentFailures > 3) {
                    health = ProviderHealth.degraded("Recent failures detected: " + recentFailures);
                    health.setResponseTimeMs(responseTime);
                }
                
                health.addDetail("recent_failures", recentFailures);
                health.addDetail("base_url", properties.getOllama().getBaseUrl());
                
                return health;
            } else {
                return ProviderHealth.unhealthy("Cannot connect to Ollama service");
            }
            
        } catch (Exception e) {
            logger.warn("Error checking Ollama health", e);
            return ProviderHealth.unknown("Health check failed: " + e.getMessage());
        }
    }
    
    @Override
    public CompletableFuture<List<String>> getSupportedModels() {
        return webClient.get()
            .uri("/api/tags")
            .retrieve()
            .bodyToMono(OllamaModelsResponse.class)
            .map(response -> response.getModels().stream()
                .map(OllamaModel::getName)
                .collect(Collectors.toList()))
            .onErrorReturn(List.of()) // Return empty list on error
            .toFuture();
    }
    
    public CompletableFuture<Void> pullModel(String modelName) {
        OllamaPullRequest pullRequest = new OllamaPullRequest();
        pullRequest.setName(modelName);
        
        return webClient.post()
            .uri("/api/pull")
            .bodyValue(pullRequest)
            .retrieve()
            .bodyToMono(String.class) // Ollama returns streaming responses, but we'll just wait for completion
            .then()
            .toFuture();
    }
    
    public CompletableFuture<Boolean> isModelAvailable(String modelName) {
        return getSupportedModels()
            .thenApply(models -> models.contains(modelName));
    }
    
    public CompletableFuture<Void> ensureModelAvailable(String modelName) {
        return isModelAvailable(modelName)
            .thenCompose(available -> {
                if (!available && properties.getOllama().isAutoModelPull()) {
                    logger.info("Model {} not available, attempting to pull", modelName);
                    return pullModel(modelName);
                }
                return CompletableFuture.completedFuture(null);
            });
    }
    
    private CompletableFuture<Boolean> testConnectivity() {
        return webClient.get()
            .uri("/api/tags")
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> true)
            .onErrorReturn(false)
            .toFuture();
    }
    
    private OllamaRequest buildOllamaRequest(String prompt, CompletionOptions options, String model) {
        OllamaRequest request = new OllamaRequest();
        request.setModel(model);
        request.setPrompt(prompt);
        request.setStream(false); // Use non-streaming for simplicity
        
        OllamaRequest.Options ollamaOptions = new OllamaRequest.Options();
        ollamaOptions.setTemperature(options.getTemperature());
        ollamaOptions.setNumPredict(options.getMaxTokens());
        request.setOptions(ollamaOptions);
        
        return request;
    }
    
    private LLMResponse mapToLLMResponse(OllamaResponse ollamaResponse, long durationMs, String model) {
        String text = ollamaResponse.getResponse() != null ? ollamaResponse.getResponse() : "";
        
        // Estimate token count (Ollama doesn't always provide exact counts)
        int tokensUsed = ollamaResponse.getEvalCount() != null ? 
            ollamaResponse.getEvalCount() : estimateTokenCount(text);
        
        LLMResponse response = new LLMResponse(text, tokensUsed, PROVIDER_NAME);
        response.addMetadata("response_time_ms", durationMs);
        response.addMetadata("model", model);
        response.addMetadata("eval_duration", ollamaResponse.getEvalDuration());
        response.addMetadata("total_duration", ollamaResponse.getTotalDuration());
        
        return response;
    }
    
    private int estimateTokenCount(String text) {
        // Rough estimation: 1 token ≈ 4 characters for English text
        return text.length() / 4;
    }
    
    private void recordUsage(LLMResponse response) {
        usageStats.incrementRequests();
        usageStats.addTokens(response.getTokensUsed());
        
        String today = LocalDateTime.now().toLocalDate().toString();
        usageStats.addDailyUsage(today, response.getTokensUsed());
        
        String model = (String) response.getMetadata().get("model");
        if (model != null) {
            usageStats.addModelUsage(model, response.getTokensUsed());
        }
        
        // Store in Redis for persistence
        try {
            String usageKey = "usage:" + PROVIDER_NAME;
            redisTemplate.opsForHash().increment(usageKey, "total_requests", 1);
            redisTemplate.opsForHash().increment(usageKey, "total_tokens", response.getTokensUsed());
            redisTemplate.opsForHash().increment(usageKey, "daily:" + today, response.getTokensUsed());
            if (model != null) {
                redisTemplate.opsForHash().increment(usageKey, "model:" + model, response.getTokensUsed());
            }
            redisTemplate.expire(usageKey, Duration.ofDays(30));
        } catch (Exception e) {
            logger.warn("Failed to record usage stats in Redis", e);
        }
    }
    
    private void recordFailure() {
        try {
            String failureKey = "health:" + PROVIDER_NAME + ":failures";
            redisTemplate.opsForValue().increment(failureKey);
            redisTemplate.expire(failureKey, Duration.ofMinutes(10)); // Failures expire after 10 minutes
        } catch (Exception e) {
            logger.warn("Failed to record failure in Redis", e);
        }
    }
    
    private String buildAnalysisPrompt(String text, AnalysisSchema schema) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following text and extract structured information in JSON format.\n\n");
        prompt.append("Text to analyze:\n").append(text).append("\n\n");
        prompt.append("Extract the following fields:\n");
        
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
        
        prompt.append("\nRespond ONLY with valid JSON containing the extracted fields. Do not include any explanatory text.");
        
        return prompt.toString();
    }
}