# Phase 1 - Detailed Implementation Guide (PRODUCTION-FIRST)

## Package 1: Infrastructure & Production Deployment Setup

### 1.1 Environment Setup + Basic Docker Foundation ✅
**Duration**: 2-3 days (COMPLETED - Expanded Scope)

#### Tasks (ACTUAL IMPLEMENTATION)
- [x] Initialize Git repository with proper .gitignore
- [x] Create project directory structure
- [x] Set up development environment documentation
- [x] Create basic Spring Boot application with health endpoint
- [x] Create Docker configuration (Dockerfile, docker-compose.yml)
- [x] Set up PostgreSQL and Redis containers
- [x] Configure container networking and health checks
- [x] Test full stack integration

#### Deliverables (ACTUAL)
```
my-contracts/
├── .gitignore                    ✅ Project-wide
├── .env.example + .env          ✅ Environment config
├── README.md                    ✅ Updated documentation
├── CLAUDE.md                    ✅ Development guidance
├── docker-compose.yml           ✅ Production containers
├── docker-compose.dev.yml       ✅ Development overrides
├── backend/                     ✅ Spring Boot API
│   ├── Dockerfile              ✅ Multi-stage production build
│   ├── Dockerfile.dev          ✅ Development container
│   ├── build.gradle.kts        ✅ Gradle build config
│   ├── settings.gradle.kts     ✅ Project settings
│   ├── gradle/wrapper/         ✅ Gradle wrapper
│   ├── gradlew + gradlew.bat   ✅ Build scripts
│   └── src/                    ✅ Java source + health endpoint
├── frontend/src/               🔲 Placeholder structure
└── infrastructure/             🔲 Placeholder directories
    ├── terraform/
    └── ansible/
```

#### Phase Completion Verification ✅ COMPLETED
- [x] **USER VERIFICATION CONFIRMED**: All deliverables meet requirements
- [x] All services start successfully via `docker-compose up -d`  
- [x] Health endpoint responds: http://localhost:3000/api/v1/health
- [x] Database connectivity confirmed
- [x] Redis connectivity confirmed

**STATUS**: Phase 1.1 OFFICIALLY COMPLETE ✅

### 1.2 Production Infrastructure Setup
**Duration**: 3-4 days (NEW - INFRASTRUCTURE FOCUS)

#### Tasks  
- [ ] Create Terraform configuration for Proxmox VM provisioning
- [ ] Set up Ansible playbooks for server configuration and security hardening
- [ ] Configure Traefik reverse proxy with SSL/TLS certificates
- [ ] Set up Prometheus + Grafana monitoring stack
- [ ] Create automated backup system for PostgreSQL
- [ ] Configure CI/CD pipeline for automated deployment
- [ ] Set up log aggregation and management
- [ ] Configure firewall and network security

#### Deliverables
```bash
infrastructure/
├── terraform/
│   ├── main.tf                 # Proxmox VM definitions  
│   ├── variables.tf            # Environment variables
│   ├── outputs.tf              # Network IPs, etc.
│   └── modules/
│       ├── vm/                 # VM module
│       └── network/            # Network configuration
├── ansible/
│   ├── site.yml                # Main deployment playbook
│   ├── inventory/
│   │   └── production.yml      # Server inventory
│   └── roles/
│       ├── docker/             # Docker installation
│       ├── security/           # Server hardening
│       ├── monitoring/         # Prometheus/Grafana
│       └── backup/             # Backup configuration
├── traefik/
│   ├── traefik.yml             # Reverse proxy config
│   └── dynamic/                # Service discovery
└── monitoring/
    ├── prometheus.yml          # Metrics collection
    ├── grafana/
    │   └── dashboards/         # Custom dashboards
    └── alertmanager.yml        # Alert rules
```

#### Phase Completion Verification
- [ ] **USER VERIFICATION REQUIRED**: Confirm infrastructure meets requirements
- [ ] Terraform successfully provisions VMs on Proxmox
- [ ] Ansible configures servers without errors
- [ ] Traefik serves HTTPS with valid certificates
- [ ] Grafana dashboards show system metrics  
- [ ] CI/CD pipeline deploys to production
- [ ] Database backups are running and tested

### ~~1.2 Advanced Docker Features & Development Tools~~ (MOVED TO 1.3)
**Duration**: 2-3 days (ADJUSTED SCOPE)

#### Tasks (REMAINING AFTER 1.1 COMPLETION)
- [ ] Add pgAdmin container for database management (development)
- [ ] Add Redis Commander for Redis management (development)  
- [ ] Implement hot reload for development containers
- [ ] Add database initialization scripts and seeding
- [ ] Configure volume mounting for development workflow
- [ ] Add container resource limits and optimization
- [ ] Create docker-compose production vs development profiles
- [ ] Add container logging and monitoring setup

#### Additional Development Services (Phase 1.2)
```yaml
# docker-compose.dev.yml additions
services:
  pgadmin:
    image: dpage/pgadmin4:latest
    ports: ["8080:80"]
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@docmgr.local
      
  redis-commander:
    image: rediscommander/redis-commander:latest
    ports: ["8081:8081"]
    environment:
      REDIS_HOSTS: local:redis:6379
```

#### Phase Completion Verification
- [ ] **USER VERIFICATION REQUIRED**: Confirm all development tools work
- [ ] pgAdmin accessible at http://localhost:8080
- [ ] Redis Commander accessible at http://localhost:8081  
- [ ] Hot reload works for backend development
- [ ] Database seeding scripts execute successfully

### 1.3 Backend API Foundation
**Duration**: 4-5 days

#### Tasks
- [ ] Initialize Spring Boot project with Gradle
- [ ] Configure Spring Web MVC with essential components
- [ ] Set up Spring Data JPA with PostgreSQL
- [ ] Create configuration properties validation (@ConfigurationProperties)
- [ ] Implement global exception handling (@ControllerAdvice)
- [ ] Set up logging with Logback and structured logging
- [ ] Create health check endpoints (Spring Actuator)
- [ ] Configure API versioning structure with @RequestMapping

#### Core Files
```java
// src/main/java/com/docmgr/Application.java - Spring Boot main class
// src/main/java/com/docmgr/config/ - Configuration classes
// src/main/java/com/docmgr/controller/ - REST controllers
// src/main/java/com/docmgr/service/ - Business logic services
// src/main/java/com/docmgr/repository/ - Data access layers
// src/main/java/com/docmgr/entity/ - JPA entities
// src/main/resources/application.yml - Configuration properties
// src/main/resources/db/migration/ - Flyway migration scripts
```

### 1.4 Database Schema & Migrations
**Duration**: 2-3 days

#### Tasks
- [ ] Design initial JPA entity classes
- [ ] Create Flyway migration scripts
- [ ] Set up database seeding with @DataJpaTest and test data
- [ ] Configure HikariCP connection pooling
- [ ] Add database backup considerations with Spring profiles

#### Initial Schema (JPA Entities)
```java
@Entity
@Table(name = "documents")
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String originalName;
    
    @Column(nullable = false)
    private String mimeType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private String filePath;
    
    @Lob
    private String textContent;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
    
    private LocalDateTime processedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status = DocumentStatus.UPLOADED;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
    
    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL)
    private DocumentAnalysis analysis;
}

@Entity
@Table(name = "llm_providers")
public class LLMProvider {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private ProviderType type;
    
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> config;
    
    @Column(nullable = false)
    private Boolean isActive = false;
}
```

---

## Package 2: LLM Service Abstraction Layer

### 2.1 Service Architecture Design
**Duration**: 2-3 days

#### Tasks
- [ ] Design abstract LLM provider interface
- [ ] Create provider factory pattern
- [ ] Design configuration management system
- [ ] Plan rate limiting strategy
- [ ] Design token usage tracking

#### Core Interfaces
```java
public interface LLMProvider {
    String getName();
    ProviderType getType();
    CompletableFuture<Boolean> isAvailable();
    CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options);
    CompletableFuture<StructuredResponse> analyze(String text, AnalysisSchema schema);
}

@Service
public interface LLMService {
    LLMProvider getProvider(String name);
    CompletableFuture<Void> switchProvider(String name);
    CompletableFuture<UsageStats> getUsageStats();
    List<LLMProvider> getAvailableProviders();
}
```

### 2.2 Gemini Provider Implementation
**Duration**: 3-4 days

#### Tasks
- [ ] Configure Spring WebClient for Gemini API calls
- [ ] Implement Gemini provider class with Spring components
- [ ] Handle API key management with @Value and Spring Security
- [ ] Implement rate limiting using Spring Data Redis and custom annotations
- [ ] Add request/response logging with Spring AOP
- [ ] Create error handling with Spring retry and circuit breakers
- [ ] Add token counting and usage tracking with Spring metrics

#### Implementation Details
```java
@Component("geminiProvider")
public class GeminiProvider implements LLMProvider {
    private final WebClient webClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final MeterRegistry meterRegistry;
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Override
    @RateLimited(provider = "gemini", requestsPerMinute = 15)
    @Retryable(value = {ApiException.class}, maxAttempts = 3)
    public CompletableFuture<LLMResponse> complete(String prompt, CompletionOptions options) {
        return webClient.post()
            .uri("/v1beta/models/gemini-1.5-flash-latest:generateContent")
            .header("Authorization", "Bearer " + apiKey)
            .bodyValue(buildRequest(prompt, options))
            .retrieve()
            .bodyToMono(GeminiResponse.class)
            .map(this::mapToLLMResponse)
            .toFuture();
    }
}
```

### 2.3 Ollama Provider Implementation
**Duration**: 3-4 days

#### Tasks
- [ ] Create Ollama HTTP client
- [ ] Implement model management (pull, list, remove)
- [ ] Handle container communication
- [ ] Add streaming response support
- [ ] Implement health checking
- [ ] Add model switching capabilities
- [ ] Create resource monitoring

#### Ollama Integration
```java
@Component("ollamaProvider")
public class OllamaProvider implements LLMProvider {
    private final WebClient webClient;
    
    @Value("${ollama.base-url}")
    private String baseUrl;
    
    @Value("${ollama.default-model}")
    private String currentModel;
    
    public CompletableFuture<Void> pullModel(String modelName) {
        return webClient.post()
            .uri(baseUrl + "/api/pull")
            .bodyValue(Map.of("name", modelName))
            .retrieve()
            .bodyToMono(Void.class)
            .toFuture();
    }
    
    public CompletableFuture<List<String>> listModels() {
        return webClient.get()
            .uri(baseUrl + "/api/tags")
            .retrieve()
            .bodyToMono(OllamaModelsResponse.class)
            .map(response -> response.getModels().stream()
                .map(model -> model.getName())
                .collect(Collectors.toList()))
            .toFuture();
    }
}
```

### 2.4 Service Layer & Configuration
**Duration**: 3-4 days

#### Tasks
- [ ] Implement LLM service orchestrator
- [ ] Create provider switching logic
- [ ] Add configuration management
- [ ] Implement usage tracking
- [ ] Create admin API endpoints
- [ ] Add provider health monitoring
- [ ] Implement fallback mechanisms

#### API Endpoints
```typescript
// LLM management endpoints
POST /api/v1/llm/complete
GET  /api/v1/llm/providers
POST /api/v1/llm/providers/:name/activate
GET  /api/v1/llm/usage
POST /api/v1/llm/test-connection
```

---

## Package 3: Document Upload & Storage

### 3.1 File Upload Infrastructure
**Duration**: 3-4 days

#### Tasks
- [ ] Configure Multer for multipart uploads
- [ ] Set up file storage directory structure
- [ ] Implement file validation (type, size, security)
- [ ] Create temporary file cleanup
- [ ] Add virus scanning integration (optional)
- [ ] Implement upload progress tracking
- [ ] Create file metadata extraction

#### Upload Configuration
```typescript
const uploadConfig = {
  dest: './uploads',
  limits: {
    fileSize: 50 * 1024 * 1024, // 50MB
    files: 10
  },
  fileFilter: (req, file, cb) => {
    // Validate file types
  }
};
```

### 3.2 PDF Processing Pipeline
**Duration**: 4-5 days

#### Tasks
- [ ] Install and configure pdf-parse
- [ ] Implement text extraction from PDFs
- [ ] Handle encrypted/password-protected PDFs
- [ ] Add metadata extraction (title, author, creation date)
- [ ] Implement page-by-page processing
- [ ] Add OCR support for scanned PDFs (Tesseract)
- [ ] Create text cleaning and normalization
- [ ] Handle multi-language documents

#### PDF Processing Service
```typescript
class PDFProcessor {
  async extractText(filePath: string): Promise<string>;
  async extractMetadata(filePath: string): Promise<PDFMetadata>;
  async extractPages(filePath: string): Promise<string[]>;
  async requiresOCR(filePath: string): Promise<boolean>;
}
```

### 3.3 Document Management API
**Duration**: 3-4 days

#### Tasks
- [ ] Create document upload endpoints
- [ ] Implement document listing with pagination
- [ ] Add document detail retrieval
- [ ] Create document deletion with cleanup
- [ ] Implement search functionality
- [ ] Add bulk operations support
- [ ] Create document sharing capabilities

#### API Implementation
```typescript
// Document endpoints
POST   /api/v1/documents/upload
GET    /api/v1/documents
GET    /api/v1/documents/:id
DELETE /api/v1/documents/:id
GET    /api/v1/documents/:id/download
POST   /api/v1/documents/bulk-upload
DELETE /api/v1/documents/bulk-delete
```

### 3.4 Storage & Security
**Duration**: 2-3 days

#### Tasks
- [ ] Implement secure file storage with access controls
- [ ] Create file cleanup routines
- [ ] Add storage usage monitoring
- [ ] Implement backup considerations
- [ ] Create file integrity checking
- [ ] Add audit logging for file operations

---

## Package 4: Mobile-First Web Interface

### 4.1 React Application Setup
**Duration**: 2-3 days

#### Tasks
- [ ] Initialize React app with Vite
- [ ] Configure TypeScript and ESLint
- [ ] Set up Tailwind CSS with mobile-first utilities
- [ ] Configure React Router for SPA navigation
- [ ] Set up React Query for state management
- [ ] Create development vs production builds
- [ ] Configure hot reload and development tools

#### Project Structure
```
frontend/src/
├── components/
│   ├── ui/ # Reusable UI components
│   ├── forms/ # Form components
│   └── layout/ # Layout components
├── pages/ # Route components
├── hooks/ # Custom React hooks
├── services/ # API services
├── types/ # TypeScript types
└── utils/ # Utility functions
```

### 4.2 PWA Configuration
**Duration**: 3-4 days

#### Tasks
- [ ] Configure PWA manifest with proper icons
- [ ] Set up service worker for caching
- [ ] Implement Web Share Target API
- [ ] Add offline support with fallback pages
- [ ] Configure install prompts
- [ ] Add push notification infrastructure
- [ ] Test PWA installation on mobile devices

#### PWA Manifest
```json
{
  "name": "Document Manager",
  "short_name": "DocMgr",
  "share_target": {
    "action": "/share",
    "method": "POST",
    "enctype": "multipart/form-data",
    "params": {
      "files": [
        {
          "name": "documents",
          "accept": ["application/pdf", "image/*"]
        }
      ]
    }
  }
}
```

### 4.3 Mobile UI Components
**Duration**: 5-6 days

#### Tasks
- [ ] Create responsive navigation component
- [ ] Build file upload component with drag-and-drop
- [ ] Implement document list with infinite scroll
- [ ] Create modal dialogs for mobile
- [ ] Build touch-friendly form components
- [ ] Add loading states and error boundaries
- [ ] Implement pull-to-refresh functionality
- [ ] Create mobile-optimized document viewer

#### Key Components
```typescript
// FileUpload.tsx - Drag-and-drop + mobile picker
// DocumentList.tsx - Infinite scroll list
// MobileNav.tsx - Touch-friendly navigation
// DocumentCard.tsx - Document preview cards
// ShareHandler.tsx - Web Share Target handler
```

### 4.4 API Integration & State Management
**Duration**: 3-4 days

#### Tasks
- [ ] Create API client with interceptors
- [ ] Implement React Query mutations and queries
- [ ] Add optimistic updates for better UX
- [ ] Create error handling and retry logic
- [ ] Implement offline data persistence
- [ ] Add real-time updates via WebSocket
- [ ] Create upload progress tracking

---

## Package 5: Basic LLM Document Processing

### 5.1 Processing Pipeline Architecture
**Duration**: 3-4 days

#### Tasks
- [ ] Design document processing workflow
- [ ] Create job queue system (Bull/Agenda)
- [ ] Implement processing status tracking
- [ ] Create WebSocket for real-time updates
- [ ] Add error handling and retry logic
- [ ] Implement processing prioritization
- [ ] Create batch processing capabilities

#### Processing Workflow
```
Upload → Text Extraction → LLM Analysis → Data Parsing → Storage → Notification
```

### 5.2 LLM Prompt Engineering
**Duration**: 4-5 days

#### Tasks
- [ ] Design prompts for document analysis
- [ ] Create structured output schemas (JSON)
- [ ] Implement entity extraction prompts
- [ ] Add document classification prompts
- [ ] Create validation for LLM responses
- [ ] Add prompt versioning and A/B testing
- [ ] Optimize prompts for different document types

#### Analysis Prompts
```typescript
const DOCUMENT_ANALYSIS_PROMPT = `
Analyze the following document and extract structured information:
- Document type (contract, invoice, letter, etc.)
- Key dates (due dates, expiration, etc.)
- Monetary amounts
- Parties involved
- Important clauses or terms

Return as JSON: {...}
`;
```

### 5.3 Structured Data Extraction
**Duration**: 3-4 days

#### Tasks
- [ ] Implement JSON schema validation
- [ ] Create data transformation layer
- [ ] Add confidence scoring for extractions
- [ ] Implement entity relationship detection
- [ ] Create data quality checks
- [ ] Add manual correction interface
- [ ] Implement extraction result caching

#### Data Schema
```typescript
interface DocumentAnalysis {
  documentType: string;
  confidence: number;
  entities: {
    dates: ExtractedDate[];
    amounts: ExtractedAmount[];
    parties: ExtractedParty[];
    terms: ExtractedTerm[];
  };
  summary: string;
  keyInsights: string[];
}
```

### 5.4 Results Display & UI Integration
**Duration**: 3-4 days

#### Tasks
- [ ] Create analysis results components
- [ ] Implement real-time processing status
- [ ] Add manual correction interface
- [ ] Create data visualization for insights
- [ ] Implement export functionality
- [ ] Add comparison views for similar documents
- [ ] Create sharing capabilities for analysis

#### UI Components
```typescript
// AnalysisResults.tsx - Display extracted data
// ProcessingStatus.tsx - Real-time status updates
// EntityEditor.tsx - Manual corrections
// InsightsPanel.tsx - Key insights display
```

---

## Package 6: Deployment & Infrastructure Automation

### 6.1 Terraform Infrastructure Setup
**Duration**: 4-5 days

#### Tasks
- [ ] Configure Terraform Proxmox provider
- [ ] Create VM templates for different services
- [ ] Design network topology and security groups
- [ ] Implement resource tagging and organization
- [ ] Create backup and disaster recovery setup
- [ ] Add monitoring and alerting infrastructure
- [ ] Configure SSL/TLS certificates

#### Terraform Structure
```
infrastructure/terraform/
├── main.tf
├── variables.tf
├── modules/
│   ├── vm/
│   ├── network/
│   └── storage/
└── environments/
    ├── dev/
    └── prod/
```

### 6.2 Ansible Configuration Management
**Duration**: 3-4 days

#### Tasks
- [ ] Create base server configuration playbook
- [ ] Configure Docker and Docker Swarm setup
- [ ] Implement security hardening playbooks
- [ ] Add monitoring agents installation
- [ ] Create application deployment playbooks
- [ ] Implement backup configuration
- [ ] Add log shipping configuration

### 6.3 CI/CD Pipeline Setup
**Duration**: 4-5 days

#### Tasks
- [ ] Configure GitHub Actions or GitLab CI
- [ ] Create automated testing pipeline
- [ ] Implement Docker image building and pushing
- [ ] Add security scanning (SAST/DAST)
- [ ] Create deployment automation
- [ ] Implement rollback capabilities
- [ ] Add deployment notifications

### 6.4 Monitoring & Observability
**Duration**: 3-4 days

#### Tasks
- [ ] Deploy Prometheus for metrics collection
- [ ] Configure Grafana dashboards
- [ ] Set up AlertManager for notifications
- [ ] Implement centralized logging with Loki
- [ ] Add application performance monitoring
- [ ] Create health check dashboards
- [ ] Configure backup monitoring

---

## Testing Strategy

### Unit Testing
- Backend API endpoints (Jest)
- LLM service providers (mocking)
- Document processing functions
- Frontend components (Vitest + Testing Library)

### Integration Testing
- Database operations
- File upload and processing
- LLM provider integrations
- API endpoint interactions

### E2E Testing
- Complete document upload workflow
- Mobile PWA functionality
- Multi-device compatibility
- Performance testing

### Load Testing
- API endpoint performance
- File upload limits
- LLM processing capacity
- Database query optimization

## Security Considerations

### Development
- Environment variable validation
- Input sanitization and validation
- File upload security
- API rate limiting
- Authentication and authorization

### Production
- Network segmentation
- SSL/TLS configuration
- Container security
- Database security
- Backup encryption
- Log sanitization

## Performance Targets

### API Response Times
- Health checks: < 100ms
- Document upload: < 5s for 10MB files
- Document listing: < 500ms
- LLM processing: < 30s for typical documents

### Mobile Performance
- First contentful paint: < 2s
- Largest contentful paint: < 4s
- Time to interactive: < 5s
- PWA install prompt: < 10s after first visit

## Documentation Requirements

### Technical Documentation
- API documentation (OpenAPI/Swagger)
- Database schema documentation
- LLM prompt documentation
- Deployment guides
- Architecture decision records (ADRs)

### User Documentation
- Installation guide
- User manual
- Mobile app usage guide
- Troubleshooting guide
- FAQ

Each package should be completed with full testing, documentation, and code review before proceeding to the next package.