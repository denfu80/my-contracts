package com.docmgr.llm.providers.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for Ollama model pull operations
 */
public class OllamaPullRequest {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("insecure")
    private Boolean insecure;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Boolean getInsecure() {
        return insecure;
    }
    
    public void setInsecure(Boolean insecure) {
        this.insecure = insecure;
    }
}