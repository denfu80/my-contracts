# Development Plan - Personal Document Management Service

## Overview
This document outlines the development strategy for building a self-hosted document management service in 5 foundational packages, followed by advanced features in Phase 2.

## Phase 1: Foundation Packages (MVP)

### Package 1: Project Foundation & Docker Setup
**Timeline**: 1-2 weeks  
**Priority**: Critical foundation

#### Deliverables
- Docker Compose configuration with multi-container setup
- Node.js/Express API server with TypeScript
- PostgreSQL database with initial schema
- Ollama container integration
- Environment configuration management
- Basic health check endpoints
- Container networking setup

#### Technical Implementation
- `docker-compose.yml` with services: api, database, ollama
- Express server with middleware (cors, helmet, morgan)
- Prisma ORM setup with database migrations
- Environment variables management (.env, validation)
- API versioning structure (/api/v1)

#### Success Criteria
- `docker-compose up` starts all services successfully
- API health endpoint responds (GET /api/v1/health)
- Database connection established
- Ollama service accessible from API container

#### File Structure
```
├── docker-compose.yml
├── backend/
│   ├── package.json
│   ├── tsconfig.json
│   ├── src/
│   │   ├── app.ts
│   │   ├── config/
│   │   └── routes/
│   └── prisma/
│       └── schema.prisma
└── .env.example
```

---

### Package 2: LLM Service Abstraction Layer
**Timeline**: 2-3 weeks  
**Priority**: Core architecture component

#### Deliverables
- Abstract LLM service interface (Strategy pattern)
- Google Gemini Flash 1.5 provider implementation
- Ollama provider implementation
- Provider factory and configuration management
- Rate limiting and request queuing
- Token usage tracking and monitoring
- Error handling and retry logic
- Provider health checking

#### Technical Implementation
- Abstract `LLMProvider` interface
- `GeminiProvider` class with Google GenAI SDK
- `OllamaProvider` class with REST API client
- `LLMService` orchestration layer
- Redis for rate limiting and caching (optional)
- Provider switching logic with fallback

#### Success Criteria
- Can switch between Gemini and Ollama providers
- Rate limiting prevents API quota exceeded
- Token usage accurately tracked
- Provider failures handled gracefully
- Simple text completion works for both providers

#### API Endpoints
- `POST /api/v1/llm/complete` - Text completion
- `GET /api/v1/llm/providers` - Available providers
- `POST /api/v1/llm/config` - Switch provider
- `GET /api/v1/llm/usage` - Token usage stats

---

### Package 3: Document Upload & Storage
**Timeline**: 2-3 weeks  
**Priority**: Core functionality

#### Deliverables
- File upload API with multipart support
- PDF text extraction pipeline
- Document metadata extraction and storage
- File storage system (local filesystem)
- Database schema for documents
- File validation and security checks
- Basic error handling and logging

#### Technical Implementation
- Multer middleware for file uploads
- pdf-parse library for PDF text extraction
- File type validation and size limits
- Document metadata schema (Prisma)
- File storage organization by date/type
- Virus scanning integration (ClamAV optional)

#### Success Criteria
- Upload PDF files via API
- Extract text content successfully
- Store document metadata in database
- Retrieve document list with metadata
- Handle upload errors gracefully

#### API Endpoints
- `POST /api/v1/documents/upload` - Upload document
- `GET /api/v1/documents` - List documents
- `GET /api/v1/documents/:id` - Get document details
- `GET /api/v1/documents/:id/content` - Download file
- `DELETE /api/v1/documents/:id` - Delete document

#### Database Schema
```sql
Documents table:
- id, filename, originalName, mimeType
- fileSize, filePath, textContent
- uploadedAt, processedAt, status
- metadata (JSON field)
```

---

### Package 4: Mobile-First Web Interface
**Timeline**: 3-4 weeks  
**Priority**: User experience foundation

#### Deliverables
- React application with TypeScript and Vite
- Mobile-first responsive design system
- Progressive Web App (PWA) configuration
- Android Web Share Target API integration
- File upload interface with drag-and-drop
- Document list and detail views
- Mobile-optimized navigation
- Offline capability basics

#### Technical Implementation
- React 18 with TypeScript
- Tailwind CSS or styled-components
- PWA manifest and service worker
- Web Share Target API configuration
- React Router for navigation
- Axios for API communication
- React Query for state management

#### Success Criteria
- Responsive design works on mobile and desktop
- PWA installable on Android devices
- Accepts shared files from other Android apps
- Drag-and-drop file upload functional
- Document list displays correctly
- Offline page loads (basic service worker)

#### Components Structure
```
src/
├── components/
│   ├── upload/FileUpload.tsx
│   ├── documents/DocumentList.tsx
│   └── layout/MobileNav.tsx
├── pages/
│   ├── Home.tsx
│   ├── Upload.tsx
│   └── Documents.tsx
├── hooks/
├── services/api.ts
└── types/
```

#### PWA Configuration
- Web app manifest with share_target
- Service worker for basic caching
- Offline fallback pages
- Install prompts for mobile users

---

### Package 5: Basic LLM Document Processing
**Timeline**: 2-3 weeks  
**Priority**: Core value proposition

#### Deliverables
- Document analysis pipeline integration
- LLM-powered entity extraction
- Document type classification
- Structured data extraction (dates, amounts, parties)
- Results storage in database
- Processing status tracking
- Basic insights display in UI
- Error handling for LLM failures

#### Technical Implementation
- Document processing queue (Bull/Agenda)
- LLM prompt engineering for document analysis
- Structured output parsing (JSON schema)
- Database schema for extracted entities
- Processing status updates via WebSocket/SSE
- Background job processing

#### Success Criteria
- Upload document triggers LLM analysis
- Extracts key entities (dates, amounts, document type)
- Classifies document category correctly
- Displays extracted information in UI
- Shows processing status to user
- Handles LLM errors gracefully

#### Processing Pipeline
1. Document upload → text extraction
2. Text → LLM analysis request
3. LLM response → structured data parsing
4. Data storage → database update
5. UI notification → results display

#### API Endpoints
- `POST /api/v1/documents/:id/analyze` - Trigger analysis
- `GET /api/v1/documents/:id/analysis` - Get analysis results
- `GET /api/v1/documents/:id/status` - Processing status
- `WebSocket /api/v1/documents/status` - Real-time updates

---

## Package Dependencies

```
Package 1 (Foundation)
    ↓
Package 2 (LLM Service) ← Can develop in parallel with Package 3
    ↓                    ↓
Package 3 (File Upload) → Package 5 (LLM Processing)
    ↓                         ↓
Package 4 (Mobile UI) --------→ Integration & Testing
```

## Phase 1 Technology Stack

### Backend
- **Runtime**: Node.js 20+ with TypeScript
- **Framework**: Express.js with middleware
- **Database**: PostgreSQL 15+ with Prisma ORM
- **LLM Integration**: Google GenAI SDK + Ollama REST API
- **File Processing**: pdf-parse, multer, sharp
- **Background Jobs**: Bull Queue with Redis
- **Validation**: Zod for schema validation

### Frontend
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite with PWA plugin
- **Styling**: Tailwind CSS with mobile-first approach
- **State Management**: React Query + Zustand
- **Routing**: React Router 6
- **HTTP Client**: Axios with interceptors

### Infrastructure
- **Containerization**: Docker Compose
- **Database**: PostgreSQL container
- **LLM**: Ollama container with model management
- **Reverse Proxy**: Nginx (optional for production)
- **Storage**: Local filesystem with Docker volumes

## Phase 2: Advanced Features (Post-MVP)

### Package 6: Advanced Document Processing
- OCR for scanned documents (Tesseract.js)
- Support for DOCX, images, email formats
- Batch processing capabilities
- Document relationship detection

### Package 7: Search & Filtering System
- Elasticsearch/PostgreSQL full-text search
- Advanced filtering and sorting
- Smart search with LLM assistance
- Saved search queries

### Package 8: Dashboard & Analytics
- Cost tracking and visualization
- Contract expiration alerts
- Document statistics and insights
- Customizable dashboard widgets

### Package 9: Email Integration
- IMAP email monitoring
- Email forwarding processing
- Attachment extraction
- Email thread analysis

### Package 10: Backup & Export System
- Automated backup scheduling
- Data export in multiple formats
- Cloud storage integration (optional)
- Data migration tools

## Development Guidelines

### Code Quality
- TypeScript strict mode enabled
- ESLint + Prettier configuration
- Unit tests with Jest/Vitest
- Integration tests for API endpoints
- E2E tests with Playwright

### Security
- Input validation and sanitization
- File upload security (type, size limits)
- API rate limiting
- Environment variable validation
- Security headers and CORS

### Performance
- Database indexing strategy
- API response caching
- Image optimization
- Bundle size optimization
- Progressive loading

### Monitoring
- Application logging (Winston)
- Error tracking (Sentry optional)
- Performance monitoring
- LLM usage analytics
- Docker health checks

## Getting Started

1. **Package 1**: Set up development environment
2. **Package 2**: Implement LLM providers
3. **Package 3**: Build file upload system
4. **Package 4**: Create mobile interface
5. **Package 5**: Connect LLM processing

Each package should be completed with tests and documentation before moving to the next.