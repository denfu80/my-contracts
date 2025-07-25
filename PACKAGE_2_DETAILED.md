# Package 2: LLM Service Abstraction Layer - Detailed Implementation

> **📋 Reference**: See [`DEVELOPMENT_PLAN.md`](DEVELOPMENT_PLAN.md) for complete package overview  
> **📚 Requirements**: See [`REQUIREMENTS.md`](REQUIREMENTS.md) for LLM provider specifications

## Overview

Package 2 implements a flexible LLM service abstraction layer that supports multiple providers (Gemini and Ollama) with seamless switching, rate limiting, and comprehensive error handling.

## Phase 2.1: Core Interface Design (Days 1-3)

### Tasks
- [ ] Design abstract LLM provider interface
- [ ] Create provider factory pattern with Spring configuration
- [ ] Design configuration management system
- [ ] Plan rate limiting strategy with Redis
- [ ] Create comprehensive error handling framework
- [ ] Design token usage tracking system

### Core Interfaces

#### LLMProvider Interface
```java
package com.docmgr.llm;

import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;

public interface LLMProvider {
    String getName();
    ProviderType getType();
    boolean isAvailable();
    CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options);
    CompletableFuture<StructuredResponse> analyze(String text, AnalysisSchema schema);
    UsageStats getUsageStats();
    ProviderHealth getHealth();
}

public enum ProviderType {
    GEMINI_API,
    OLLAMA_LOCAL
}
```

#### LLM Service Orchestrator
```java
package com.docmgr.llm;

@Service
public interface LLMService {
    CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options);
    LLMProvider getActiveProvider();
    List<LLMProvider> getAvailableProviders();
    CompletableFuture<Void> switchProvider(String providerName);
    UsageStats getAggregatedUsage();
    ProviderHealth getProviderHealth(String providerName);
}
```

#### Response Models
```java
public class LLMResponse {
    private String text;
    private int tokensUsed;
    private LocalDateTime timestamp;
    private String providerId;
    private Map<String, Object> metadata;
}

public class CompletionOptions {
    private int maxTokens = 1000;
    private double temperature = 0.7;
    private String model; // Optional model override
    private boolean stream = false;
}

public class UsageStats {
    private int totalRequests;
    private int totalTokens;
    private LocalDateTime lastRequest;
    private Map<String, Integer> dailyUsage;
}
```

### Deliverables Phase 2.1
- Core interfaces in `src/main/java/com/docmgr/llm/`
- Spring configuration classes
- Basic service structure
- Comprehensive JavaDoc documentation

---

## Phase 2.2: Gemini Provider Implementation (Days 4-7)

### Tasks
- [ ] Configure Spring WebClient for Gemini API
- [ ] Implement Gemini provider with authentication
- [ ] Add request/response mapping
- [ ] Implement rate limiting (15 requests/minute)
- [ ] Add error handling and retry logic
- [ ] Implement token counting and usage tracking
- [ ] Add comprehensive logging with Spring AOP

### Gemini Provider Implementation
```java
package com.docmgr.llm.providers;

@Component("geminiProvider")
@ConditionalOnProperty(name = "app.llm.gemini.enabled", havingValue = "true")
public class GeminiProvider implements LLMProvider {
    
    private final WebClient webClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final MeterRegistry meterRegistry;
    
    @Value("${app.llm.gemini.api-key}")
    private String apiKey;
    
    @Value("${app.llm.gemini.base-url}")
    private String baseUrl;
    
    public GeminiProvider(WebClient.Builder webClientBuilder, 
                         RedisTemplate<String, String> redisTemplate,
                         MeterRegistry meterRegistry) {
        this.webClient = webClientBuilder
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();
        this.redisTemplate = redisTemplate;
        this.meterRegistry = meterRegistry;
    }
    
    @Override
    @RateLimited(provider = "gemini", requestsPerMinute = 15)
    @Retryable(value = {ApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options) {
        return webClient.post()
            .uri("/v1beta/models/gemini-1.5-flash-latest:generateContent")
            .bodyValue(buildGeminiRequest(prompt, options))
            .retrieve()
            .onStatus(HttpStatus::isError, this::handleError)
            .bodyToMono(GeminiApiResponse.class)
            .map(this::mapToLLMResponse)
            .doOnSuccess(response -> recordUsage(response))
            .toFuture();
    }
    
    private GeminiRequest buildGeminiRequest(String prompt, CompletionOptions options) {
        return GeminiRequest.builder()
            .contents(List.of(Content.builder()
                .parts(List.of(Part.builder().text(prompt).build()))
                .build()))
            .generationConfig(GenerationConfig.builder()
                .maxOutputTokens(options.getMaxTokens())
                .temperature(options.getTemperature())
                .build())
            .build();
    }
}
```

### Rate Limiting Implementation
```java
package com.docmgr.llm.ratelimit;

@Aspect
@Component
public class RateLimitingAspect {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = "rate_limit:" + rateLimited.provider();
        String windowKey = key + ":" + getCurrentMinute();
        
        String currentCount = redisTemplate.opsForValue().get(windowKey);
        int requestCount = currentCount != null ? Integer.parseInt(currentCount) : 0;
        
        if (requestCount >= rateLimited.requestsPerMinute()) {
            throw new RateLimitExceededException("Rate limit exceeded for provider: " + rateLimited.provider());
        }
        
        redisTemplate.opsForValue().increment(windowKey);
        redisTemplate.expire(windowKey, Duration.ofMinutes(1));
        
        return joinPoint.proceed();
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimited {
    String provider();
    int requestsPerMinute();
}
```

### Deliverables Phase 2.2
- Complete Gemini provider implementation
- Rate limiting with Redis backend
- Error handling and retry mechanisms
- Token usage tracking
- Comprehensive unit tests

---

## Phase 2.3: Ollama Provider Implementation (Days 8-11)

### Tasks
- [ ] Create Ollama HTTP client for existing container
- [ ] Implement model management (list, switch models)
- [ ] Add streaming response support
- [ ] Implement health checking
- [ ] Add model switching capabilities
- [ ] Create resource monitoring
- [ ] Test integration with existing Ollama container

### Ollama Provider Implementation
```java
package com.docmgr.llm.providers;

@Component("ollamaProvider")
@ConditionalOnProperty(name = "app.llm.ollama.enabled", havingValue = "true", matchIfMissing = true)
public class OllamaProvider implements LLMProvider {
    
    private final WebClient webClient;
    private final OllamaModelManager modelManager;
    
    @Value("${app.llm.ollama.base-url:http://ollama:11434}")
    private String baseUrl;
    
    @Value("${app.llm.ollama.default-model:llama2}")
    private String defaultModel;
    
    public OllamaProvider(WebClient.Builder webClientBuilder, OllamaModelManager modelManager) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.modelManager = modelManager;
    }
    
    @Override
    public CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options) {
        String model = options.getModel() != null ? options.getModel() : defaultModel;
        
        return webClient.post()
            .uri("/api/generate")
            .bodyValue(OllamaRequest.builder()
                .model(model)
                .prompt(prompt)
                .stream(false)
                .options(OllamaOptions.builder()
                    .temperature(options.getTemperature())
                    .numPredict(options.getMaxTokens())
                    .build())
                .build())
            .retrieve()
            .bodyToMono(OllamaResponse.class)
            .map(this::mapToLLMResponse)
            .doOnSuccess(response -> recordUsage(response))
            .toFuture();
    }
    
    public CompletableFuture<List<String>> listAvailableModels() {
        return webClient.get()
            .uri("/api/tags")
            .retrieve()
            .bodyToMono(OllamaModelsResponse.class)
            .map(response -> response.getModels().stream()
                .map(OllamaModel::getName)
                .collect(Collectors.toList()))
            .toFuture();
    }
    
    public CompletableFuture<Void> pullModel(String modelName) {
        return webClient.post()
            .uri("/api/pull")
            .bodyValue(OllamaPullRequest.builder().name(modelName).build())
            .retrieve()
            .bodyToMono(Void.class)
            .toFuture();
    }
}
```

### Model Management Service
```java
package com.docmgr.llm.ollama;

@Service
public class OllamaModelManager {
    
    private final OllamaProvider ollamaProvider;
    private final RedisTemplate<String, String> redisTemplate;
    
    public CompletableFuture<List<String>> getAvailableModels() {
        return ollamaProvider.listAvailableModels();
    }
    
    public CompletableFuture<Void> ensureModelAvailable(String modelName) {
        return getAvailableModels()
            .thenCompose(models -> {
                if (!models.contains(modelName)) {
                    return ollamaProvider.pullModel(modelName);
                }
                return CompletableFuture.completedFuture(null);
            });
    }
    
    public void setActiveModel(String modelName) {
        redisTemplate.opsForValue().set("ollama:active_model", modelName);
    }
    
    public String getActiveModel() {
        return redisTemplate.opsForValue().get("ollama:active_model");
    }
}
```

### Deliverables Phase 2.3
- Complete Ollama provider implementation
- Model management capabilities
- Integration with existing Ollama container
- Health monitoring
- Comprehensive unit tests

---

## Phase 2.4: Service Layer & API Integration (Days 12-14)

### Tasks
- [ ] Implement LLM service orchestrator
- [ ] Create provider switching logic
- [ ] Add configuration management
- [ ] Implement usage tracking and metrics
- [ ] Create REST API endpoints
- [ ] Add provider health monitoring
- [ ] Implement fallback mechanisms

### LLM Service Implementation
```java
package com.docmgr.llm.service;

@Service
@Transactional
public class LLMServiceImpl implements LLMService {
    
    private final Map<String, LLMProvider> providers;
    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    
    @Value("${app.llm.default-provider:ollama}")
    private String defaultProvider;
    
    public LLMServiceImpl(List<LLMProvider> providerList, 
                         RedisTemplate<String, String> redisTemplate,
                         ApplicationEventPublisher eventPublisher) {
        this.providers = providerList.stream()
            .collect(Collectors.toMap(LLMProvider::getName, Function.identity()));
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options) {
        LLMProvider activeProvider = getActiveProvider();
        
        return activeProvider.complete(prompt, options)
            .handle((response, throwable) -> {
                if (throwable != null) {
                    return handleProviderFailure(activeProvider, prompt, options, throwable);
                }
                return CompletableFuture.completedFuture(response);
            })
            .thenCompose(Function.identity());
    }
    
    private CompletableFuture<LLMResponse> handleProviderFailure(
            LLMProvider failedProvider, String prompt, CompletionOptions options, Throwable error) {
        
        eventPublisher.publishEvent(new ProviderFailureEvent(failedProvider.getName(), error));
        
        // Try fallback provider
        String fallbackProviderName = getFallbackProvider(failedProvider.getName());
        if (fallbackProviderName != null) {
            LLMProvider fallbackProvider = providers.get(fallbackProviderName);
            return fallbackProvider.complete(prompt, options);
        }
        
        return CompletableFuture.failedFuture(error);
    }
    
    @Override
    public LLMProvider getActiveProvider() {
        String activeProviderName = redisTemplate.opsForValue().get("llm:active_provider");
        if (activeProviderName == null) {
            activeProviderName = defaultProvider;
        }
        
        LLMProvider provider = providers.get(activeProviderName);
        if (provider == null || !provider.isAvailable()) {
            provider = getFirstAvailableProvider();
        }
        
        return provider;
    }
}
```

### REST API Controller
```java
package com.docmgr.api.llm;

@RestController
@RequestMapping("/api/v1/llm")
@Validated
public class LLMController {
    
    private final LLMService llmService;
    
    @PostMapping("/complete")
    public CompletableFuture<ResponseEntity<LLMResponse>> complete(
            @Valid @RequestBody CompletionRequest request) {
        
        CompletionOptions options = CompletionOptions.builder()
            .maxTokens(request.getMaxTokens())
            .temperature(request.getTemperature())
            .model(request.getModel())
            .build();
            
        return llmService.complete(request.getPrompt(), options)
            .thenApply(ResponseEntity::ok)
            .exceptionally(throwable -> 
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    @GetMapping("/providers")
    public ResponseEntity<List<ProviderInfo>> getProviders() {
        List<ProviderInfo> providers = llmService.getAvailableProviders().stream()
            .map(provider -> ProviderInfo.builder()
                .name(provider.getName())
                .type(provider.getType())
                .available(provider.isAvailable())
                .health(provider.getHealth())
                .build())
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(providers);
    }
    
    @PostMapping("/providers/{providerName}/activate")
    public CompletableFuture<ResponseEntity<Void>> activateProvider(
            @PathVariable String providerName) {
        
        return llmService.switchProvider(providerName)
            .thenApply(v -> ResponseEntity.ok().<Void>build())
            .exceptionally(throwable -> 
                ResponseEntity.badRequest().<Void>build());
    }
    
    @GetMapping("/usage")
    public ResponseEntity<UsageStats> getUsageStats() {
        UsageStats stats = llmService.getAggregatedUsage();
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/test")
    public CompletableFuture<ResponseEntity<ProviderTestResult>> testProvider(
            @RequestParam(required = false) String providerName) {
        
        String testPrompt = "Test connection. Respond with 'OK'.";
        CompletionOptions options = CompletionOptions.builder()
            .maxTokens(10)
            .temperature(0.1)
            .build();
            
        if (providerName != null) {
            options.setModel(providerName);
        }
        
        return llmService.complete(testPrompt, options)
            .thenApply(response -> ResponseEntity.ok(
                ProviderTestResult.success(response.getProviderId(), response.getText())))
            .exceptionally(throwable -> ResponseEntity.ok(
                ProviderTestResult.failure(providerName, throwable.getMessage())));
    }
}
```

### Configuration Properties
```java
package com.docmgr.config;

@ConfigurationProperties(prefix = "app.llm")
@Data
public class LLMProperties {
    
    private String defaultProvider = "ollama";
    private boolean fallbackEnabled = true;
    
    private Gemini gemini = new Gemini();
    private Ollama ollama = new Ollama();
    
    @Data
    public static class Gemini {
        private boolean enabled = false;
        private String apiKey;
        private String baseUrl = "https://generativelanguage.googleapis.com";
        private int rateLimitPerMinute = 15;
        private int maxRetries = 3;
    }
    
    @Data
    public static class Ollama {
        private boolean enabled = true;
        private String baseUrl = "http://ollama:11434";
        private String defaultModel = "llama2";
        private int timeoutSeconds = 30;
    }
}
```

### Deliverables Phase 2.4
- Complete LLM service orchestrator
- REST API endpoints
- Configuration management
- Provider switching and fallback logic
- Usage tracking and metrics
- Integration tests

---

## Package 2 Success Criteria

### Functional Requirements
- ✅ Can switch between Gemini and Ollama providers seamlessly
- ✅ Rate limiting prevents API quota violations
- ✅ Token usage accurately tracked and reported
- ✅ Provider failures handled gracefully with fallbacks
- ✅ Simple text completion works for both providers
- ✅ All endpoints accessible via production deployment

### Technical Requirements
- ✅ Spring Boot integration with proper configuration
- ✅ Redis integration for rate limiting and caching
- ✅ Comprehensive error handling and logging
- ✅ Unit and integration test coverage > 80%
- ✅ API documentation with OpenAPI/Swagger
- ✅ Production deployment via git-based process

### API Endpoints Summary
```
POST /api/v1/llm/complete              - Text completion
GET  /api/v1/llm/providers             - List available providers
POST /api/v1/llm/providers/{name}/activate - Switch active provider
GET  /api/v1/llm/usage                 - Token usage statistics
POST /api/v1/llm/test                  - Test provider connectivity
```

## Testing Strategy

### Unit Tests
- Provider implementation tests with mocked HTTP clients
- Service layer tests with mocked providers
- Rate limiting logic tests
- Configuration management tests

### Integration Tests
- End-to-end API tests with TestContainers
- Redis integration tests
- Ollama container integration tests
- Error handling and fallback scenarios

### Manual Testing
- Test with actual Gemini API (if key available)
- Test with existing Ollama container
- Provider switching scenarios
- Rate limiting behavior

## Deployment

After Package 2 completion:
```bash
# Commit and push changes
git add .
git commit -m "implement Package 2: LLM Service Abstraction Layer"
git push origin main

# Deploy to production
ssh root@192.168.4.8
cd /opt/docmgr
./scripts/deploy.sh

# Verify deployment
curl http://192.168.4.8:3000/api/v1/llm/providers
```

**Next**: Package 3 - Document Upload & Storage