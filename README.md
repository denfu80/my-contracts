# Personal Document Management Service

A self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology.

## Project Status

**Current Phase**: Package 1.1 - Environment Setup ✅  
**Implementation Progress**: Foundation & Docker Setup

## Quick Start

### Development Environment

1. **Prerequisites**
   - Java 21+
   - Docker & Docker Compose
   - Git

2. **Clone and Setup**
   ```bash
   git clone <repository-url>
   cd my-contracts
   
   # Copy environment variables
   cp .env.example .env
   
   # Edit .env file with your configuration
   nano .env
   ```

3. **Start Services**
   ```bash
   # Start development environment with hot reload
   docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
   
   # Or start production environment
   docker-compose up
   ```

4. **Access Services**
   - API: http://localhost:3000
   - Health Check: http://localhost:3000/api/v1/health
   - pgAdmin: http://localhost:8080 (dev only)
   - Redis Commander: http://localhost:8081 (dev only)

### Manual Backend Development

```bash
cd backend

# Run tests
./gradlew test

# Start application (requires PostgreSQL and Redis running)
./gradlew bootRun
```

## Architecture

### Technology Stack
- **Backend**: Java 21 + Spring Boot 3.x + Spring Data JPA
- **Database**: PostgreSQL 15 with Flyway migrations
- **Cache**: Redis 7
- **LLM**: Gemini Flash 1.5 API + Ollama (local)
- **Document Processing**: Apache Tika + PDFBox + Tesseract OCR
- **Infrastructure**: Docker Compose

### Services

| Service | Port | Description |
|---------|------|-------------|
| API | 3000 | Spring Boot REST API |
| PostgreSQL | 5432 | Primary database |
| Redis | 6379 | Caching and rate limiting |
| Ollama | 11434 | Local LLM processing |
| pgAdmin | 8080 | Database admin (dev only) |
| Redis Commander | 8081 | Redis admin (dev only) |

## Development

### Project Structure
```
├── backend/                 # Spring Boot API
│   ├── src/main/java/       # Java source code
│   ├── src/main/resources/  # Configuration and migrations
│   └── src/test/            # Tests
├── frontend/                # React PWA (Package 4)
├── infrastructure/          # Terraform & Ansible (Package 6)
├── docker-compose.yml       # Production containers
└── docker-compose.dev.yml   # Development overrides
```

### Key Features (Planned)
- **Document Upload**: Web interface + email forwarding
- **LLM Analysis**: Contract/bill entity extraction
- **Search**: Full-text search with AI categorization
- **Mobile PWA**: Android Share API integration
- **Privacy**: Local-first processing, no external data transmission

### Database Schema
- `documents` - File metadata and content
- `llm_providers` - LLM service configurations
- `document_analysis` - AI processing results
- `llm_usage_stats` - Usage tracking and costs

## Next Steps

**Package 1 Remaining Tasks:**
- [ ] Fix Gradle wrapper download
- [ ] Test Docker Compose setup
- [ ] Verify database migrations
- [ ] Configure LLM providers

**Upcoming Packages:**
- **Package 2**: LLM Service Abstraction Layer
- **Package 3**: Document Upload & Storage
- **Package 4**: Mobile-First Web Interface
- **Package 5**: LLM Document Processing
- **Package 6**: Deployment & Infrastructure

## Configuration

Key environment variables (see `.env.example`):
- `POSTGRES_*` - Database connection
- `GEMINI_API_KEY` - Google Gemini API key
- `OLLAMA_*` - Local LLM configuration
- `UPLOAD_MAX_SIZE` - File upload limits

## Documentation

- `REQUIREMENTS.md` - Complete project requirements
- `DEVELOPMENT_PLAN.md` - 6-package implementation roadmap  
- `PHASE_1_DETAILED.md` - Granular implementation tasks
- `CLAUDE.md` - Claude Code development guidance