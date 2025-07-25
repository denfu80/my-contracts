package com.docmgr.llm.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Schema definition for structured document analysis
 */
public class AnalysisSchema {
    
    private String schemaType;
    private Map<String, FieldDefinition> fields = new HashMap<>();
    private String instructions;
    
    public AnalysisSchema() {}
    
    public AnalysisSchema(String schemaType) {
        this.schemaType = schemaType;
    }
    
    public static AnalysisSchema forDocumentType(String documentType) {
        return new AnalysisSchema(documentType);
    }
    
    public String getSchemaType() {
        return schemaType;
    }
    
    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }
    
    public Map<String, FieldDefinition> getFields() {
        return fields;
    }
    
    public void setFields(Map<String, FieldDefinition> fields) {
        this.fields = fields;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public AnalysisSchema addField(String name, String type, boolean required) {
        this.fields.put(name, new FieldDefinition(type, required));
        return this;
    }
    
    public AnalysisSchema addField(String name, String type, boolean required, String description) {
        this.fields.put(name, new FieldDefinition(type, required, description));
        return this;
    }
    
    public AnalysisSchema withInstructions(String instructions) {
        this.instructions = instructions;
        return this;
    }
    
    public static class FieldDefinition {
        private String type;
        private boolean required;
        private String description;
        
        public FieldDefinition() {}
        
        public FieldDefinition(String type, boolean required) {
            this.type = type;
            this.required = required;
        }
        
        public FieldDefinition(String type, boolean required, String description) {
            this.type = type;
            this.required = required;
            this.description = description;
        }
        
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