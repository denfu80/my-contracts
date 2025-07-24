# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Personal Document Management Service - a self-hosted Java/Spring Boot application for automatically processing contracts, bills, and emails using LLM technology. Currently implementing Package 1 (Backend Foundation).

## Architecture & Tech Stack

- **Backend**: Java 21 + Spring Boot 3.4.7 + Spring Data JPA + PostgreSQL 15
- **Cache**: Redis 7 + Spring Data Redis  
- **LLM Integration**: Gemini Flash 1.5 API + Ollama (llama3.1) with strategy pattern
- **Document Processing**: Apache Tika 2.9.1 + PDFBox 3.0.1
- **Database**: PostgreSQL with Flyway migrations
- **Infrastructure**: Docker Compose with health checks
- **Testing**: JUnit 5 + TestContainers + H2 (test database)
- **Code Quality**: Spotless (Google Java Format)

## Current Status

**Phase**: Package 1 - Backend Foundation ✅ (CORRECTED)
**Implementation Progress**: Spring Boot API, Docker services, database schema
**Next**: Package 2 - LLM Service Abstraction Layer

## Development Commands

### Docker Development (Recommended)
```bash
# Start all services with development overrides
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# View logs for specific service
docker-compose logs -f api
docker-compose logs -f database

# Stop all services
docker-compose down

# Rebuild API service after code changes
docker-compose build api && docker-compose up -d api
```

### Backend Development (Java/Spring Boot)
```bash
cd backend

# Run application locally (requires PostgreSQL/Redis running)
./gradlew bootRun

# Run tests with TestContainers
./gradlew test

# Run specific test class
./gradlew test --tests "com.docmgr.controller.HealthControllerTest"

# Format code with Spotless
./gradlew spotlessApply

# Check code formatting
./gradlew spotlessCheck

# Build JAR
./gradlew build

# Run Flyway migrations manually
./gradlew flywayMigrate
```

### Database Operations
```bash
# Access PostgreSQL via Docker
docker exec -it docmgr-postgres psql -U docmgr_user -d docmgr

# Access pgAdmin (dev environment)
# Navigate to http://localhost:8080

# Access Redis Commander (dev environment)  
# Navigate to http://localhost:8081
```

## Key Architecture Patterns

### Document Processing Pipeline
1. **Upload/Email** → PDF text extraction (Tika/PDFBox) → OCR if needed
2. **LLM Analysis** → Entity extraction → Classification → Structured storage
3. **Search & Retrieval** → Full-text search → AI-powered categorization

### LLM Service Abstraction
- Strategy pattern for switching between Gemini API and Ollama
- Rate limiting and token usage tracking
- Async processing with PostgreSQL job queue

### Security & Privacy Model
- **Local-first**: All data processing happens locally
- **Zero external transmission**: Complete local control of documents
- **Containerized deployment**: Docker isolation + HTTPS/TLS

## Mobile-First PWA Design

- **Android Share API integration**: Accept shared files from mobile
- **Responsive design**: Tailwind CSS with mobile-first breakpoints
- **Offline capability**: Service workers + local storage
- **Performance targets**: Sub-second search, 95%+ document accuracy

## Development Guidelines

- Use Context7 MCP for latest API documentation
- TypeScript strict mode with comprehensive testing
- Follow Spring Boot conventions and security best practices
- Test with TestContainers for integration tests
- Never commit secrets or API keys

## Project Documentation

- `REQUIREMENTS.md` - Complete functional specifications
- `DEVELOPMENT_PLAN.md` - 6-package implementation roadmap
- `PHASE_1_DETAILED.md` - Granular implementation tasks
- `PROJECT_CONTEXT.md` - Current status and next actions