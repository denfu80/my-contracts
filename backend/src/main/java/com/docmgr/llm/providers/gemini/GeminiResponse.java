package com.docmgr.llm.providers.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response model for Gemini API responses
 */
public class GeminiResponse {
    
    @JsonProperty("candidates")
    private List<Candidate> candidates;
    
    @JsonProperty("usageMetadata")
    private UsageMetadata usageMetadata;
    
    @JsonProperty("promptFeedback")
    private PromptFeedback promptFeedback;
    
    public List<Candidate> getCandidates() {
        return candidates;
    }
    
    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
    
    public UsageMetadata getUsageMetadata() {
        return usageMetadata;
    }
    
    public void setUsageMetadata(UsageMetadata usageMetadata) {
        this.usageMetadata = usageMetadata;
    }
    
    public PromptFeedback getPromptFeedback() {
        return promptFeedback;
    }
    
    public void setPromptFeedback(PromptFeedback promptFeedback) {
        this.promptFeedback = promptFeedback;
    }
    
    public static class Candidate {
        @JsonProperty("content")
        private Content content;
        
        @JsonProperty("finishReason")
        private String finishReason;
        
        @JsonProperty("index")
        private Integer index;
        
        @JsonProperty("safetyRatings")
        private List<SafetyRating> safetyRatings;
        
        public Content getContent() {
            return content;
        }
        
        public void setContent(Content content) {
            this.content = content;
        }
        
        public String getFinishReason() {
            return finishReason;
        }
        
        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }
        
        public Integer getIndex() {
            return index;
        }
        
        public void setIndex(Integer index) {
            this.index = index;
        }
        
        public List<SafetyRating> getSafetyRatings() {
            return safetyRatings;
        }
        
        public void setSafetyRatings(List<SafetyRating> safetyRatings) {
            this.safetyRatings = safetyRatings;
        }
    }
    
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;
        
        @JsonProperty("role")
        private String role;
        
        public List<Part> getParts() {
            return parts;
        }
        
        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
    
    public static class Part {
        @JsonProperty("text")
        private String text;
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
    }
    
    public static class UsageMetadata {
        @JsonProperty("promptTokenCount")
        private Integer promptTokenCount;
        
        @JsonProperty("candidatesTokenCount")
        private Integer candidatesTokenCount;
        
        @JsonProperty("totalTokenCount")
        private Integer totalTokenCount;
        
        public Integer getPromptTokenCount() {
            return promptTokenCount;
        }
        
        public void setPromptTokenCount(Integer promptTokenCount) {
            this.promptTokenCount = promptTokenCount;
        }
        
        public Integer getCandidatesTokenCount() {
            return candidatesTokenCount;
        }
        
        public void setCandidatesTokenCount(Integer candidatesTokenCount) {
            this.candidatesTokenCount = candidatesTokenCount;
        }
        
        public Integer getTotalTokenCount() {
            return totalTokenCount;
        }
        
        public void setTotalTokenCount(Integer totalTokenCount) {
            this.totalTokenCount = totalTokenCount;
        }
    }
    
    public static class SafetyRating {
        @JsonProperty("category")
        private String category;
        
        @JsonProperty("probability")
        private String probability;
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getProbability() {
            return probability;
        }
        
        public void setProbability(String probability) {
            this.probability = probability;
        }
    }
    
    public static class PromptFeedback {
        @JsonProperty("safetyRatings")
        private List<SafetyRating> safetyRatings;
        
        public List<SafetyRating> getSafetyRatings() {
            return safetyRatings;
        }
        
        public void setSafetyRatings(List<SafetyRating> safetyRatings) {
            this.safetyRatings = safetyRatings;
        }
    }
}