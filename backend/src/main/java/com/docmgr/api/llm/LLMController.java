package com.docmgr.api.llm;

import com.docmgr.llm.LLMProvider;
import com.docmgr.llm.model.*;
import com.docmgr.llm.service.LLMService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * REST API controller for LLM operations
 */
@RestController
@RequestMapping("/api/v1/llm")
@Validated
public class LLMController {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMController.class);
    
    private final LLMService llmService;
    
    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }
    
    @PostMapping("/complete")
    public CompletableFuture<ResponseEntity<LLMResponse>> complete(
            @Valid @RequestBody CompletionRequest request) {
        
        logger.info("Received completion request for prompt length: {}", request.getPrompt().length());
        
        CompletionOptions options = CompletionOptions.builder()
            .maxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 1000)
            .temperature(request.getTemperature() != null ? request.getTemperature() : 0.7)
            .model(request.getModel())
            .build();
            
        return llmService.complete(request.getPrompt(), options)
            .thenApply(response -> {
                logger.debug("Completion successful, tokens used: {}", response.getTokensUsed());
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                logger.error("Completion failed", throwable);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }
    
    @PostMapping("/analyze")
    public CompletableFuture<ResponseEntity<StructuredResponse>> analyze(
            @Valid @RequestBody AnalysisRequest request) {
        
        logger.info("Received analysis request for text length: {}", request.getText().length());
        
        AnalysisSchema schema = AnalysisSchema.forDocumentType(request.getDocumentType());
        if (request.getFields() != null) {
            request.getFields().forEach((name, field) -> 
                schema.addField(name, field.getType(), field.isRequired(), field.getDescription()));
        }
        if (request.getInstructions() != null) {
            schema.withInstructions(request.getInstructions());
        }
        
        return llmService.analyze(request.getText(), schema)
            .thenApply(response -> {
                logger.debug("Analysis successful, extracted {} fields", response.getData().size());
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                logger.error("Analysis failed", throwable);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
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
    
    @GetMapping("/providers/active")
    public ResponseEntity<ProviderInfo> getActiveProvider() {
        try {
            LLMProvider activeProvider = llmService.getActiveProvider();
            ProviderInfo info = ProviderInfo.builder()
                .name(activeProvider.getName())
                .type(activeProvider.getType())
                .available(activeProvider.isAvailable())
                .health(activeProvider.getHealth())
                .build();
                
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            logger.error("Failed to get active provider", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/providers/{providerName}/activate")
    public CompletableFuture<ResponseEntity<Void>> activateProvider(
            @PathVariable String providerName) {
        
        logger.info("Attempting to activate provider: {}", providerName);
        
        return llmService.switchProvider(providerName)
            .thenApply(v -> {
                logger.info("Successfully activated provider: {}", providerName);
                return ResponseEntity.ok().<Void>build();
            })
            .exceptionally(throwable -> {
                logger.error("Failed to activate provider: " + providerName, throwable);
                return ResponseEntity.badRequest().<Void>build();
            });
    }
    
    @GetMapping("/usage")
    public ResponseEntity<UsageStats> getUsageStats() {
        UsageStats stats = llmService.getAggregatedUsage();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, ProviderHealth>> getProvidersHealth() {
        Map<String, ProviderHealth> healthMap = llmService.getAllProviderHealth();
        return ResponseEntity.ok(healthMap);
    }
    
    @PostMapping("/test")
    public CompletableFuture<ResponseEntity<ProviderTestResult>> testProvider(
            @RequestParam(required = false) String providerName) {
        
        CompletableFuture<Boolean> testFuture;
        String targetProvider = providerName;
        
        if (providerName != null) {
            logger.info("Testing specific provider: {}", providerName);
            testFuture = llmService.testProvider(providerName);
        } else {
            logger.info("Testing active provider");
            testFuture = llmService.testActiveProvider();
            try {
                targetProvider = llmService.getActiveProvider().getName();
            } catch (Exception e) {
                targetProvider = "unknown";
            }
        }
        
        final String finalTargetProvider = targetProvider;
        
        return testFuture
            .thenApply(success -> {
                if (success) {
                    logger.info("Provider test successful: {}", finalTargetProvider);
                    return ResponseEntity.ok(ProviderTestResult.success(finalTargetProvider, "Connection successful"));
                } else {
                    logger.warn("Provider test failed: {}", finalTargetProvider);
                    return ResponseEntity.ok(ProviderTestResult.failure(finalTargetProvider, "Connection failed"));
                }
            })
            .exceptionally(throwable -> {
                logger.error("Provider test error: " + finalTargetProvider, throwable);
                return ResponseEntity.ok(ProviderTestResult.failure(finalTargetProvider, throwable.getMessage()));
            });
    }
    
    @GetMapping("/models")
    public CompletableFuture<ResponseEntity<List<String>>> getSupportedModels(
            @RequestParam(required = false) String providerName) {
        
        try {
            LLMProvider provider;
            if (providerName != null) {
                provider = llmService.getAvailableProviders().stream()
                    .filter(p -> p.getName().equals(providerName))
                    .findFirst()
                    .orElse(null);
                if (provider == null) {
                    return CompletableFuture.completedFuture(
                        ResponseEntity.badRequest().build());
                }
            } else {
                provider = llmService.getActiveProvider();
            }
            
            return provider.getSupportedModels()
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    logger.error("Failed to get supported models", throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
                
        } catch (Exception e) {
            logger.error("Error getting supported models", e);
            return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }
}