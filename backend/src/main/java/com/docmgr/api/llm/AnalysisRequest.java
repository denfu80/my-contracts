package com.docmgr.api.llm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Request DTO for structured document analysis API
 */
public class AnalysisRequest {
    
    @NotBlank(message = "Text cannot be empty")
    @Size(max = 100000, message = "Text cannot exceed 100,000 characters")
    private String text;
    
    @NotBlank(message = "Document type cannot be empty")
    private String documentType;
    
    private Map<String, FieldRequest> fields;
    
    private String instructions;
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    public Map<String, FieldRequest> getFields() {
        return fields;
    }
    
    public void setFields(Map<String, FieldRequest> fields) {
        this.fields = fields;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public static class FieldRequest {
        @NotBlank(message = "Field type cannot be empty")
        private String type;
        
        private boolean required = false;
        
        private String description;
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public boolean isRequired() {
            return required;
        }
        
        public void setRequired(boolean required) {
            this.required = required;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
}