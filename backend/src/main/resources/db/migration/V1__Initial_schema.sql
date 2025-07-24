-- Initial schema for Personal Document Management Service
-- V1__Initial_schema.sql

-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Document status enum
CREATE TYPE document_status AS ENUM (
    'UPLOADED',
    'PROCESSING',
    'PROCESSED',
    'FAILED',
    'ARCHIVED'
);

-- LLM provider type enum
CREATE TYPE provider_type AS ENUM (
    'GEMINI',
    'OLLAMA',
    'OPENAI'
);

-- Documents table
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    filename VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    text_content TEXT,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE,
    status document_status NOT NULL DEFAULT 'UPLOADED',
    metadata JSONB,
    
    CONSTRAINT documents_file_size_positive CHECK (file_size > 0)
);

-- LLM providers table
CREATE TABLE llm_providers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    type provider_type NOT NULL,
    config JSONB NOT NULL DEFAULT '{}',
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Document analysis table
CREATE TABLE document_analysis (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    provider_id UUID NOT NULL REFERENCES llm_providers(id),
    document_type VARCHAR(100),
    confidence_score DECIMAL(5,4) CHECK (confidence_score >= 0 AND confidence_score <= 1),
    extracted_entities JSONB DEFAULT '{}',
    summary TEXT,
    key_insights TEXT[],
    processing_time_ms INTEGER,
    analyzed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(document_id)  -- One analysis per document
);

-- Usage statistics table for LLM providers
CREATE TABLE llm_usage_stats (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    provider_id UUID NOT NULL REFERENCES llm_providers(id) ON DELETE CASCADE,
    request_count INTEGER NOT NULL DEFAULT 0,
    token_count BIGINT NOT NULL DEFAULT 0,
    total_cost DECIMAL(10,6) DEFAULT 0,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(provider_id, date)
);

-- Create indexes for better query performance
CREATE INDEX idx_documents_status ON documents(status);
CREATE INDEX idx_documents_uploaded_at ON documents(uploaded_at DESC);
CREATE INDEX idx_documents_mime_type ON documents(mime_type);
CREATE INDEX idx_documents_metadata_gin ON documents USING GIN (metadata);

CREATE INDEX idx_document_analysis_document_id ON document_analysis(document_id);
CREATE INDEX idx_document_analysis_provider_id ON document_analysis(provider_id);
CREATE INDEX idx_document_analysis_document_type ON document_analysis(document_type);
CREATE INDEX idx_document_analysis_confidence ON document_analysis(confidence_score DESC);

CREATE INDEX idx_llm_providers_active ON llm_providers(is_active);
CREATE INDEX idx_llm_providers_type ON llm_providers(type);

CREATE INDEX idx_usage_stats_provider_date ON llm_usage_stats(provider_id, date);

-- Insert default LLM providers
INSERT INTO llm_providers (name, type, config, is_active) VALUES
('gemini-flash-1.5', 'GEMINI', '{"api_url": "https://generativelanguage.googleapis.com", "model": "gemini-1.5-flash-latest", "max_tokens": 8192}', false),
('ollama-local', 'OLLAMA', '{"base_url": "http://ollama:11434", "model": "llama3.1", "max_tokens": 4096}', true);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to automatically update updated_at
CREATE TRIGGER update_llm_providers_updated_at
    BEFORE UPDATE ON llm_providers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();