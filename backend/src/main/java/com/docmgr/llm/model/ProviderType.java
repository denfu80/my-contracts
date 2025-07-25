package com.docmgr.llm.model;

/**
 * Enumeration of supported LLM provider types
 */
public enum ProviderType {
    /**
     * Google Gemini API provider (cloud-based)
     */
    GEMINI_API,
    
    /**
     * Ollama provider (local container)
     */
    OLLAMA_LOCAL
}