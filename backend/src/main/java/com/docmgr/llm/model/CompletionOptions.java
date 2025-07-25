package com.docmgr.llm.model;

/**
 * Configuration options for LLM text completion requests
 */
public class CompletionOptions {
    
    private int maxTokens = 1000;
    private double temperature = 0.7;
    private String model;
    private boolean stream = false;
    
    public CompletionOptions() {}
    
    public CompletionOptions(int maxTokens, double temperature) {
        this.maxTokens = maxTokens;
        this.temperature = temperature;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public boolean isStream() {
        return stream;
    }
    
    public void setStream(boolean stream) {
        this.stream = stream;
    }
    
    public static class Builder {
        private final CompletionOptions options = new CompletionOptions();
        
        public Builder maxTokens(int maxTokens) {
            options.setMaxTokens(maxTokens);
            return this;
        }
        
        public Builder temperature(double temperature) {
            options.setTemperature(temperature);
            return this;
        }
        
        public Builder model(String model) {
            options.setModel(model);
            return this;
        }
        
        public Builder stream(boolean stream) {
            options.setStream(stream);
            return this;
        }
        
        public CompletionOptions build() {
            return options;
        }
    }
}