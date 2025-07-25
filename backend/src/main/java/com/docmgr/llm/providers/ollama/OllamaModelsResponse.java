package com.docmgr.llm.providers.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response model for Ollama models list API
 */
public class OllamaModelsResponse {
    
    @JsonProperty("models")
    private List<OllamaModel> models;
    
    public List<OllamaModel> getModels() {
        return models;
    }
    
    public void setModels(List<OllamaModel> models) {
        this.models = models;
    }
}