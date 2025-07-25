package com.docmgr.llm.providers.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model information from Ollama API
 */
public class OllamaModel {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("size")
    private Long size;
    
    @JsonProperty("digest")
    private String digest;
    
    @JsonProperty("modified_at")
    private String modifiedAt;
    
    @JsonProperty("details")
    private Details details;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getSize() {
        return size;
    }
    
    public void setSize(Long size) {
        this.size = size;
    }
    
    public String getDigest() {
        return digest;
    }
    
    public void setDigest(String digest) {
        this.digest = digest;
    }
    
    public String getModifiedAt() {
        return modifiedAt;
    }
    
    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    public Details getDetails() {
        return details;
    }
    
    public void setDetails(Details details) {
        this.details = details;
    }
    
    public static class Details {
        @JsonProperty("format")
        private String format;
        
        @JsonProperty("family")
        private String family;
        
        @JsonProperty("families")
        private String[] families;
        
        @JsonProperty("parameter_size")
        private String parameterSize;
        
        @JsonProperty("quantization_level")
        private String quantizationLevel;
        
        public String getFormat() {
            return format;
        }
        
        public void setFormat(String format) {
            this.format = format;
        }
        
        public String getFamily() {
            return family;
        }
        
        public void setFamily(String family) {
            this.family = family;
        }
        
        public String[] getFamilies() {
            return families;
        }
        
        public void setFamilies(String[] families) {
            this.families = families;
        }
        
        public String getParameterSize() {
            return parameterSize;
        }
        
        public void setParameterSize(String parameterSize) {
            this.parameterSize = parameterSize;
        }
        
        public String getQuantizationLevel() {
            return quantizationLevel;
        }
        
        public void setQuantizationLevel(String quantizationLevel) {
            this.quantizationLevel = quantizationLevel;
        }
    }
}