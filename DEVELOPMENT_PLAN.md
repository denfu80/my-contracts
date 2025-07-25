# Development Plan - Personal Document Management Service

> **📋 Foundation Reference**: This document references [`REQUIREMENTS.md`](REQUIREMENTS.md) for complete feature specifications and technical requirements.

## Overview
This document outlines the development strategy for building a self-hosted document management service in 5 packages, with git-based deployment after each completion.

**📚 Complete project requirements**: See [`REQUIREMENTS.md`](REQUIREMENTS.md)  
**🎯 Current status**: Package 1 complete, Package 2 next

## Current Status

### ✅ Package 1: Production Infrastructure - COMPLETED
**Timeline**: 30 minutes actual (git-based deployment)  
**Status**: Production deployment running successfully

- ✅ Spring Boot API with health endpoints
- ✅ PostgreSQL database with secure configuration  
- ✅ Redis caching layer
- ✅ Git-based deployment automation
- ✅ Backup scripts and production procedures

---

## 🎯 Package 2: LLM Service Abstraction Layer - NEXT
**Timeline**: 2-3 weeks  
**Priority**: Core functionality foundation

### Deliverables
- Abstract LLM service interface (Strategy pattern)
- Google Gemini Flash 1.5 provider implementation
- Ollama provider implementation (connecting to existing container)
- Provider factory and configuration management
- Rate limiting with Redis integration
- Token usage tracking
- Error handling and retry logic
- Provider health checking and switching

### Technical Implementation
- Abstract `LLMProvider` interface with Spring components
- `GeminiProvider` class using Spring WebClient for API calls
- `OllamaProvider` class connecting to existing Ollama container
- `LLMService` orchestration layer with Spring Service annotations
- Redis integration with Spring Data Redis for rate limiting
- Provider switching logic with Spring configuration profiles
- Spring AOP for request/response logging and metrics

### Success Criteria
- Can switch between Gemini and Ollama providers seamlessly
- Rate limiting prevents API quota violations
- Token usage accurately tracked and reported
- Provider failures handled gracefully with fallbacks
- Simple text completion works for both providers
- All endpoints accessible via production deployment

### API Endpoints
```
POST /api/v1/llm/complete        - Text completion
GET  /api/v1/llm/providers       - List available providers
POST /api/v1/llm/providers/:name/activate - Switch active provider
GET  /api/v1/llm/usage          - Token usage statistics
POST /api/v1/llm/test           - Test provider connectivity
```

---

## Package 3: Document Upload & Storage
**Timeline**: 2-3 weeks  
**Priority**: Core functionality

### Deliverables
- File upload API with multipart support
- PDF text extraction pipeline (Apache Tika + PDFBox)
- Document metadata extraction and storage
- File storage system (local filesystem with Docker volumes)
- Database schema for documents (JPA entities)
- File validation and security checks
- Basic error handling and logging

### Technical Implementation
- Spring Multipart for file uploads with size/type validation
- Apache Tika for universal document text extraction
- PDFBox for advanced PDF processing and metadata extraction
- JPA entities for document metadata with Spring Data repositories
- File storage organization using Spring Resource abstraction

### Success Criteria
- Upload PDF files via API successfully
- Extract text content from various document formats
- Store document metadata in database
- Retrieve document list with metadata
- Handle upload errors gracefully
- Secure file access controls

### API Endpoints
```
POST   /api/v1/documents/upload         - Upload document
GET    /api/v1/documents               - List documents with pagination
GET    /api/v1/documents/:id           - Get document details
GET    /api/v1/documents/:id/content   - Download original file
GET    /api/v1/documents/:id/text      - Get extracted text
DELETE /api/v1/documents/:id           - Delete document
```

---

## Package 4: Mobile-First Web Interface
**Timeline**: 3-4 weeks  
**Priority**: User experience

### Deliverables
- React application with TypeScript and Vite
- Mobile-first responsive design system
- Progressive Web App (PWA) configuration
- Android Web Share Target API integration
- File upload interface with drag-and-drop
- Document list and detail views
- Mobile-optimized navigation
- Offline capability basics

### Technical Implementation
- React 18 with TypeScript
- Tailwind CSS for mobile-first responsive design
- PWA manifest and service worker
- Web Share Target API configuration
- React Router for navigation
- React Query for API state management
- Axios for HTTP communication

### Success Criteria
- Responsive design works on mobile and desktop
- PWA installable on Android devices
- Accepts shared files from other Android apps
- Drag-and-drop file upload functional
- Document list displays correctly with search
- Basic offline functionality

---

## Package 5: LLM Document Processing
**Timeline**: 2-3 weeks  
**Priority**: Core value proposition

### Deliverables
- Document analysis pipeline integration
- LLM-powered entity extraction
- Document type classification
- Structured data extraction (dates, amounts, parties)
- Results storage in database
- Processing status tracking
- Error handling for LLM failures

### Technical Implementation
- Spring Async with @Async for background processing
- Redis-based task queuing with Spring Data Redis
- LLM prompt engineering with structured JSON responses
- JPA entities for extracted document data and analysis results
- WebSocket support using Spring WebSocket/STOMP for real-time updates
- Spring Events for decoupled processing pipeline

### Success Criteria
- Upload document triggers automatic LLM analysis
- Extracts key entities (dates, amounts, document type)
- Classifies document category correctly
- Displays extracted information in UI
- Shows processing status to user
- Handles LLM errors gracefully with fallbacks

---

## Package Dependencies

```
Package 1 (Production Infrastructure) ✅ COMPLETED
    ↓ Git-based deployment ready
Package 2 (LLM Service Abstraction) 🎯 NEXT
    ↓ Deploy via git pull → Production v2
Package 3 (Document Upload & Storage)
    ↓ Deploy via git pull → Production v3  
Package 4 (Mobile-First Web Interface)
    ↓ Deploy via git pull → Production v4
Package 5 (LLM Document Processing)
    ↓ Deploy via git pull → Production v5 (MVP)
```

**Key Principle**: After each package completion, the system is deployed to production and fully functional for that feature set.

## Technology Stack

### Backend
- **Runtime**: Java 21 with Spring Boot 3.x
- **Framework**: Spring Web MVC with Spring Security
- **Database**: PostgreSQL 15+ with Spring Data JPA
- **LLM Integration**: Java HTTP clients for Gemini API + Ollama REST
- **File Processing**: Apache Tika, PDFBox, Spring Multipart
- **Background Jobs**: Spring Async + Redis with Spring Data Redis
- **Validation**: Jakarta Bean Validation with Hibernate Validator

### Frontend
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite with PWA plugin
- **Styling**: Tailwind CSS with mobile-first approach
- **State Management**: React Query + Zustand
- **Routing**: React Router 6
- **HTTP Client**: Axios with interceptors

### Infrastructure
- **Deployment**: Git-based with automated scripts
- **Containerization**: Docker Compose with multi-stage builds
- **Database**: PostgreSQL container with backup automation
- **LLM**: Existing Ollama container integration
- **Storage**: Local filesystem with Docker volumes

## Development Guidelines

### Code Quality
- Java code follows Spring Boot conventions
- TypeScript strict mode enabled
- Unit tests with JUnit 5 + Spring Boot Test
- Integration tests for API endpoints
- Component tests for React

### Security
- Input validation and sanitization
- File upload security (type, size limits)
- API rate limiting via Redis
- Environment variable validation
- Security headers and CORS

### Performance
- Database indexing strategy
- API response caching
- Efficient file handling
- Progressive loading for mobile

Each package should be completed with tests and documentation before moving to the next.