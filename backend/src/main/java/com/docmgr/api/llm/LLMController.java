package com.docmgr.api.llm;

import com.docmgr.llm.LLMProvider;
import com.docmgr.llm.model.*;
import com.docmgr.llm.service.LLMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "LLM Operations", description = "Language Model operations with Gemini and Ollama providers")
public class LLMController {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMController.class);
    
    private final LLMService llmService;
    
    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }
    
    @Operation(
        summary = "Generate text completion",
        description = "Generate text completion using the active LLM provider (Gemini or Ollama)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Text completion generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "LLM provider error or service unavailable")
    })
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
    
    @Operation(
        summary = "Analyze document text",
        description = "Extract structured information from document text using LLM analysis"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document analyzed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid document text or schema"),
        @ApiResponse(responseCode = "500", description = "Analysis failed or provider unavailable")
    })
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
    
    @Operation(
        summary = "List available LLM providers",
        description = "Get information about all available LLM providers and their health status"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider list retrieved successfully")
    })
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
    
    @Operation(
        summary = "Get active LLM provider",
        description = "Get information about the currently active LLM provider"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Active provider information retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Failed to get active provider information")
    })
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
    
    @Operation(
        summary = "Activate LLM provider",
        description = "Switch to a specific LLM provider (gemini or ollama)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider activated successfully"),
        @ApiResponse(responseCode = "400", description = "Provider not found or unavailable"),
        @ApiResponse(responseCode = "500", description = "Failed to activate provider")
    })
    @PostMapping("/providers/{providerName}/activate")
    public CompletableFuture<ResponseEntity<Void>> activateProvider(
            @Parameter(description = "Provider name (gemini or ollama)", example = "gemini")
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
    
    @Operation(
        summary = "Get LLM usage statistics",
        description = "Get aggregated usage statistics including token counts and request metrics"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usage statistics retrieved successfully")
    })
    @GetMapping("/usage")
    public ResponseEntity<UsageStats> getUsageStats() {
        UsageStats stats = llmService.getAggregatedUsage();
        return ResponseEntity.ok(stats);
    }
    
    @Operation(
        summary = "Get provider health status",
        description = "Check health status of all LLM providers"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Health status retrieved successfully")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, ProviderHealth>> getProvidersHealth() {
        Map<String, ProviderHealth> healthMap = llmService.getAllProviderHealth();
        return ResponseEntity.ok(healthMap);
    }
    
    @Operation(
        summary = "Test LLM provider connection",
        description = "Test connectivity and basic functionality of a specific provider or the active provider"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Test completed (check result.success for actual status)"),
        @ApiResponse(responseCode = "500", description = "Test execution failed")
    })
    @PostMapping("/test")
    public CompletableFuture<ResponseEntity<ProviderTestResult>> testProvider(
            @Parameter(description = "Provider name to test (gemini or ollama). If not specified, tests the active provider", example = "gemini")
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
    
    @Operation(
        summary = "Get supported models",
        description = "Get list of supported models for a specific provider or the active provider"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Supported models retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Provider not found"),
        @ApiResponse(responseCode = "500", description = "Failed to retrieve models")
    })
    @GetMapping("/models")
    public CompletableFuture<ResponseEntity<List<String>>> getSupportedModels(
            @Parameter(description = "Provider name to get models for (gemini or ollama). If not specified, uses active provider", example = "ollama")
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