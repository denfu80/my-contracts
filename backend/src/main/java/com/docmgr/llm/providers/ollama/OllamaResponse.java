package com.docmgr.llm.providers.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for Ollama API responses
 */
public class OllamaResponse {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("response")
    private String response;
    
    @JsonProperty("done")
    private Boolean done;
    
    @JsonProperty("context")
    private int[] context;
    
    @JsonProperty("total_duration")
    private Long totalDuration;
    
    @JsonProperty("load_duration")
    private Long loadDuration;
    
    @JsonProperty("prompt_eval_count")
    private Integer promptEvalCount;
    
    @JsonProperty("prompt_eval_duration")
    private Long promptEvalDuration;
    
    @JsonProperty("eval_count")
    private Integer evalCount;
    
    @JsonProperty("eval_duration")
    private Long evalDuration;
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public Boolean getDone() {
        return done;
    }
    
    public void setDone(Boolean done) {
        this.done = done;
    }
    
    public int[] getContext() {
        return context;
    }
    
    public void setContext(int[] context) {
        this.context = context;
    }
    
    public Long getTotalDuration() {
        return totalDuration;
    }
    
    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }
    
    public Long getLoadDuration() {
        return loadDuration;
    }
    
    public void setLoadDuration(Long loadDuration) {
        this.loadDuration = loadDuration;
    }
    
    public Integer getPromptEvalCount() {
        return promptEvalCount;
    }
    
    public void setPromptEvalCount(Integer promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }
    
    public Long getPromptEvalDuration() {
        return promptEvalDuration;
    }
    
    public void setPromptEvalDuration(Long promptEvalDuration) {
        this.promptEvalDuration = promptEvalDuration;
    }
    
    public Integer getEvalCount() {
        return evalCount;
    }
    
    public void setEvalCount(Integer evalCount) {
        this.evalCount = evalCount;
    }
    
    public Long getEvalDuration() {
        return evalDuration;
    }
    
    public void setEvalDuration(Long evalDuration) {
        this.evalDuration = evalDuration;
    }
}