package com.docmgr.llm.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Usage statistics for LLM provider tracking
 */
public class UsageStats {
    
    private int totalRequests;
    private int totalTokens;
    private LocalDateTime lastRequest;
    private Map<String, Integer> dailyUsage = new HashMap<>();
    private Map<String, Integer> modelUsage = new HashMap<>();
    private double totalCost = 0.0;
    
    public UsageStats() {}
    
    public UsageStats(int totalRequests, int totalTokens) {
        this.totalRequests = totalRequests;
        this.totalTokens = totalTokens;
    }
    
    public int getTotalRequests() {
        return totalRequests;
    }
    
    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }
    
    public int getTotalTokens() {
        return totalTokens;
    }
    
    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }
    
    public LocalDateTime getLastRequest() {
        return lastRequest;
    }
    
    public void setLastRequest(LocalDateTime lastRequest) {
        this.lastRequest = lastRequest;
    }
    
    public Map<String, Integer> getDailyUsage() {
        return dailyUsage;
    }
    
    public void setDailyUsage(Map<String, Integer> dailyUsage) {
        this.dailyUsage = dailyUsage;
    }
    
    public Map<String, Integer> getModelUsage() {
        return modelUsage;
    }
    
    public void setModelUsage(Map<String, Integer> modelUsage) {
        this.modelUsage = modelUsage;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public void incrementRequests() {
        this.totalRequests++;
        this.lastRequest = LocalDateTime.now();
    }
    
    public void addTokens(int tokens) {
        this.totalTokens += tokens;
    }
    
    public void addDailyUsage(String date, int tokens) {
        this.dailyUsage.merge(date, tokens, Integer::sum);
    }
    
    public void addModelUsage(String model, int tokens) {
        this.modelUsage.merge(model, tokens, Integer::sum);
    }
    
    public void addCost(double cost) {
        this.totalCost += cost;
    }
    
    public double getAverageTokensPerRequest() {
        return totalRequests > 0 ? (double) totalTokens / totalRequests : 0.0;
    }
}