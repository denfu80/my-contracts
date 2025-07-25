# Development Plan - Personal Document Management Service

> **📋 Foundation Reference**: This document references [`REQUIREMENTS.md`](REQUIREMENTS.md) for complete feature specifications and technical requirements.

## Overview
This document outlines the development strategy for building a self-hosted document management service in 5 foundational packages, followed by advanced features in Phase 2.

**📚 Complete project requirements**: See [`REQUIREMENTS.md`](REQUIREMENTS.md)  
**⚡ Current implementation tasks**: See [`PHASE_1_DETAILED.md`](PHASE_1_DETAILED.md)

## Phase 1: Foundation Packages (MVP) - REORGANIZED FOR PRODUCTION-FIRST

### Package 1: Infrastructure & Production Deployment Setup ✅ COMPLETED
**Timeline**: 30 minutes (ACTUAL - Git-based deployment)  
**Priority**: Critical foundation

#### Deliverables (SIMPLIFIED)
- **Production Docker Compose** configuration without development tools
- **Git-based deployment** to existing Proxmox Docker node
- **Basic Spring Boot API** with health endpoints ✅ COMPLETE
- **PostgreSQL database** with initial schema ✅ COMPLETE
- **Redis caching layer** for performance and session management ✅ COMPLETE
- **Environment configuration** template for production
- **Simple backup solution** for database
- **Update/deployment procedure** documentation

#### Technical Implementation (SIMPLIFIED)
- **Git-Based Deployment**: Clone repository on existing Docker node, git pull for updates
- **Production Docker Compose**: Clean configuration without dev tools (pgAdmin, Redis Commander)
- **Container Management**: Standard Docker Compose with health checks and restart policies
- **Environment Management**: .env file configuration for production settings
- **Simple Backup**: Basic PostgreSQL dump script with rotation
- **Update Process**: Git pull + container restart for deployments

#### Success Criteria (SIMPLIFIED)
- **Deployment**: Application running on existing Proxmox Docker node
- **Access**: API accessible from intranet (GET /api/v1/health)
- **Services**: All containers start and pass health checks
- **Updates**: Simple git pull + restart process works
- **Backup**: Database backup script functional
- **Documentation**: Clear deployment and update procedures

#### File Structure (Production-First)
```
├── infrastructure/
│   ├── terraform/
│   │   ├── main.tf              # Proxmox VMs
│   │   ├── variables.tf         # Environment configs
│   │   └── modules/
│   └── ansible/
│       ├── site.yml             # Main playbook
│       ├── roles/
│       │   ├── docker/          # Docker installation
│       │   ├── security/        # Server hardening
│       │   └── monitoring/      # Prometheus setup
├── docker-compose.yml           # Production services
├── docker-compose.monitoring.yml # Grafana, Prometheus
├── traefik/
│   └── traefik.yml             # Reverse proxy config
├── .github/workflows/
│   └── deploy.yml              # CI/CD pipeline
├── backend/                    # Basic Spring Boot API (Package 1.1 DONE)
└── monitoring/
    ├── prometheus.yml
    └── grafana/dashboards/
```

---

### Package 2: LLM Service Abstraction Layer
**Timeline**: 2-3 weeks  
**Priority**: Core functionality - extends Package 1 infrastructure

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
- Abstract `LLMProvider` interface with Spring components
- `GeminiProvider` class using Spring WebClient for API calls
- `OllamaProvider` class with Spring HTTP client
- `LLMService` orchestration layer with Spring Service annotations
- Redis integration with Spring Data Redis for rate limiting
- Provider switching logic with Spring configuration profiles

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
- Spring Multipart for file uploads with size/type validation
- Apache Tika for universal document text extraction
- PDFBox for advanced PDF processing and metadata extraction
- JPA entities for document metadata with Spring Data repositories
- File storage organization using Spring Resource abstraction
- Optional virus scanning integration with ClamAV

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

#### Database Schema (JPA Entities)
```java
@Entity
@Table(name = "documents")
public class Document {
    @Id @GeneratedValue
    private UUID id;
    private String filename;
    private String originalName;
    private String mimeType;
    private Long fileSize;
    private String filePath;
    @Lob private String textContent;
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
}
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
- Spring Async with @Async for background processing
- Redis-based task queuing with Spring Data Redis
- LLM prompt engineering with structured JSON responses
- JPA entities for extracted document data and analysis results
- WebSocket support using Spring WebSocket/STOMP for real-time updates
- Spring Events for decoupled processing pipeline

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

## Package Dependencies (PRODUCTION-FIRST APPROACH - SIMPLIFIED)

```
Package 1 (Production Infrastructure) ✅ COMPLETED (30 min)
    ↓ (Deploy after each package via git pull)
Package 2 (LLM Service Abstraction) 🎯 NEXT → Production Deployment v2
    ↓ (Deploy after each package via git pull)
Package 3 (Document Upload & Storage) → Production Deployment v3
    ↓ (Deploy after each package via git pull)
Package 4 (Mobile-First Web Interface) → Production Deployment v4
    ↓ (Deploy after each package via git pull)
Package 5 (LLM Document Processing) → Production Deployment v5 (MVP)
```

**Key Principle**: After each package completion, the system is deployed to production and fully functional for that feature set.

## Phase 1 Technology Stack

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
- **Containerization**: Docker Compose with multi-stage Java builds
- **Database**: PostgreSQL container
- **LLM**: Ollama container with model management
- **Build Tool**: Gradle with Docker integration
- **Storage**: Local filesystem with Docker volumes

### Package 6: Deployment & Infrastructure Automation
**Timeline**: 2-3 weeks  
**Priority**: Production readiness

#### Deliverables
- Terraform configuration for Proxmox VM provisioning
- Ansible playbooks for server configuration
- CI/CD pipeline (GitHub Actions or GitLab CI)
- Docker Swarm or Kubernetes setup for clustering
- Monitoring and logging stack (Prometheus, Grafana, Loki)
- Backup automation for Proxmox environment
- SSL/TLS certificate management (Let's Encrypt)
- Network security configuration

#### Technical Implementation
- **Infrastructure as Code**: Terraform Proxmox provider
- **Configuration Management**: Ansible for server setup
- **Container Orchestration**: Docker Swarm (simpler) or K3s (lightweight Kubernetes)
- **Load Balancing**: HAProxy or Traefik for multi-node setup
- **Monitoring**: Prometheus + Grafana + AlertManager
- **Logging**: Loki + Promtail for centralized logs
- **Backup**: Proxmox Backup Server integration

#### Success Criteria
- Automated VM creation on Proxmox nodes
- One-command deployment to production
- Multi-node clustering operational
- Monitoring dashboards functional
- Automated backups running
- SSL certificates auto-renewed

#### Infrastructure Components
```
Proxmox Cluster:
├── Node 1 (Primary)
│   ├── Document Management VMs
│   ├── Database cluster node
│   └── Monitoring stack
├── Node 2 (Secondary - optional)
│   ├── Load balancer
│   ├── Database replica
│   └── Backup services
└── Shared Storage (Ceph/NFS)
```

#### Deployment Pipeline
1. **Code Push** → Git repository
2. **CI Build** → Docker images + tests
3. **Infrastructure** → Terraform provisions VMs
4. **Configuration** → Ansible configures services
5. **Deployment** → Docker Swarm/K3s updates
6. **Monitoring** → Health checks and alerts

## Phase 2: Advanced Features (Post-MVP)

### Package 7: Advanced Document Processing
- OCR for scanned documents (Tesseract.js)
- Support for DOCX, images, email formats
- Batch processing capabilities
- Document relationship detection

### Package 8: Search & Filtering System
- Elasticsearch/PostgreSQL full-text search
- Advanced filtering and sorting
- Smart search with LLM assistance
- Saved search queries

### Package 9: Dashboard & Analytics
- Cost tracking and visualization
- Contract expiration alerts
- Document statistics and insights
- Customizable dashboard widgets

### Package 10: Email Integration
- IMAP email monitoring
- Email forwarding processing
- Attachment extraction
- Email thread analysis

### Package 11: Backup & Export System
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