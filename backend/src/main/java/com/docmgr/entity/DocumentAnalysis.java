package com.docmgr.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "document_analysis")
public class DocumentAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private LLMProvider provider;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "confidence_score", precision = 5, scale = 4)
    private BigDecimal confidenceScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_entities")
    private Map<String, Object> extractedEntities;

    @Lob
    private String summary;

    @Column(name = "key_insights")
    private List<String> keyInsights;

    @Column(name = "processing_time_ms")
    private Integer processingTimeMs;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt = LocalDateTime.now();

    // Constructors
    public DocumentAnalysis() {}

    public DocumentAnalysis(Document document, LLMProvider provider) {
        this.document = document;
        this.provider = provider;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public LLMProvider getProvider() {
        return provider;
    }

    public void setProvider(LLMProvider provider) {
        this.provider = provider;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Map<String, Object> getExtractedEntities() {
        return extractedEntities;
    }

    public void setExtractedEntities(Map<String, Object> extractedEntities) {
        this.extractedEntities = extractedEntities;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getKeyInsights() {
        return keyInsights;
    }

    public void setKeyInsights(List<String> keyInsights) {
        this.keyInsights = keyInsights;
    }

    public Integer getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Integer processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    @Override
    public String toString() {
        return "DocumentAnalysis{" +
                "id=" + id +
                ", documentType='" + documentType + '\'' +
                ", confidenceScore=" + confidenceScore +
                ", analyzedAt=" + analyzedAt +
                '}';
    }
}