package com.docmgr.llm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for LLM providers
 */
@Component
@ConfigurationProperties(prefix = "app.llm")
public class LLMProperties {
    
    private String defaultProvider = "ollama";
    private boolean fallbackEnabled = true;
    private int defaultMaxTokens = 1000;
    private double defaultTemperature = 0.7;
    
    private final Gemini gemini = new Gemini();
    private final Ollama ollama = new Ollama();
    
    public String getDefaultProvider() {
        return defaultProvider;
    }
    
    public void setDefaultProvider(String defaultProvider) {
        this.defaultProvider = defaultProvider;
    }
    
    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }
    
    public void setFallbackEnabled(boolean fallbackEnabled) {
        this.fallbackEnabled = fallbackEnabled;
    }
    
    public int getDefaultMaxTokens() {
        return defaultMaxTokens;
    }
    
    public void setDefaultMaxTokens(int defaultMaxTokens) {
        this.defaultMaxTokens = defaultMaxTokens;
    }
    
    public double getDefaultTemperature() {
        return defaultTemperature;
    }
    
    public void setDefaultTemperature(double defaultTemperature) {
        this.defaultTemperature = defaultTemperature;
    }
    
    public Gemini getGemini() {
        return gemini;
    }
    
    public Ollama getOllama() {
        return ollama;
    }
    
    public static class Gemini {
        private boolean enabled = false;
        private String apiKey;
        private String baseUrl = "https://generativelanguage.googleapis.com";
        private String model = "gemini-1.5-flash-latest";
        private int rateLimitPerMinute = 15;
        private int maxRetries = 3;
        private long timeoutSeconds = 30;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getApiKey() {
            return apiKey;
        }
        
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
        
        public int getRateLimitPerMinute() {
            return rateLimitPerMinute;
        }
        
        public void setRateLimitPerMinute(int rateLimitPerMinute) {
            this.rateLimitPerMinute = rateLimitPerMinute;
        }
        
        public int getMaxRetries() {
            return maxRetries;
        }
        
        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
        
        public long getTimeoutSeconds() {
            return timeoutSeconds;
        }
        
        public void setTimeoutSeconds(long timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }
    }
    
    public static class Ollama {
        private boolean enabled = true;
        private String baseUrl = "http://ollama:11434";
        private String defaultModel = "llama3.1";
        private long timeoutSeconds = 30;
        private boolean autoModelPull = true;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public String getDefaultModel() {
            return defaultModel;
        }
        
        public void setDefaultModel(String defaultModel) {
            this.defaultModel = defaultModel;
        }
        
        public long getTimeoutSeconds() {
            return timeoutSeconds;
        }
        
        public void setTimeoutSeconds(long timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }
        
        public boolean isAutoModelPull() {
            return autoModelPull;
        }
        
        public void setAutoModelPull(boolean autoModelPull) {
            this.autoModelPull = autoModelPull;
        }
    }
}