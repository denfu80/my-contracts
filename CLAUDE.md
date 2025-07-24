# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Personal Document Management Service - a self-hosted Java/Spring Boot application for automatically processing contracts, bills, and emails using LLM technology. Currently implementing Package 1 (Backend Foundation).

## Architecture & Tech Stack

- **Backend**: Java 17+ + Spring Boot 3.5.4 + Spring AI 1.0.0 + PostgreSQL 15
- **Cache**: Redis 7 + Spring Data Redis  
- **LLM Integration**: Gemini Flash 1.5 API + External Ollama service with strategy pattern
- **Document Processing**: Spring AI Tika Document Reader + Apache Tika + PDFBox
- **Database**: PostgreSQL with Flyway migrations
- **Infrastructure**: Docker Compose with health checks
- **Testing**: JUnit 5 + Spring Boot Test
- **Container Orchestration**: Docker Compose with health checks

## External Services

- **Ollama**: Will be deployed as separate service on your network (Package 2)
- **LLM Providers**: External API endpoints (Gemini) and local Ollama instance

## Current Status

**Phase**: Package 1 - Backend Foundation ✅ (CORRECTED)
**Implementation Progress**: Spring Boot API, Docker services, database schema
**Next**: Package 2 - LLM Service Abstraction Layer

## Development Commands

### Docker Development (Recommended)
```bash
# Phase 1.1: Start core services (PostgreSQL + Redis + API)
docker-compose up -d

# View logs for specific service
docker-compose logs -f api
docker-compose logs -f database

# Stop all services
docker-compose down

# Rebuild API service after code changes
docker-compose build api && docker-compose up -d api

# Development with hot reload (Package 1.2)
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
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

# Access Redis via Docker
docker exec -it docmgr-redis redis-cli

# Development tools (pgAdmin, Redis Commander) available in Package 1.2
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
- Follow Spring Boot conventions and security best practices
- Use JUnit 5 for testing with Spring Boot Test framework
- Never commit secrets or API keys
- Package structure follows `org.mycontract.backend` namespace

## Phase Discipline & Boundary Control

### ⚠️ CRITICAL: Stick to Phase Boundaries
- **NEVER implement future phase tasks** without explicit user approval
- **ALWAYS verify phase completion** with user before proceeding
- **EACH PHASE** has specific deliverables - do not expand scope

### Phase Verification Protocol
1. Complete only tasks listed in current phase
2. **MANDATORY**: Present phase completion summary to user
3. **WAIT** for user confirmation before next phase
4. Update documentation to reflect actual implementation

### Current Phase Status
- **Phase 1.1**: ✅ OFFICIALLY COMPLETE ✅ (Environment + Basic Docker + API)
- **Phase 1.2**: 🎯 READY TO START (Advanced Docker + Dev Tools)
- **Phase 1.3**: 🔲 PENDING (Backend API Foundation)

### Phase Scope Reminders
- **Phase 1.1**: Project structure + Basic Spring Boot + Core containers
- **Phase 1.2**: Development tools + Advanced Docker features
- **Phase 1.3**: API endpoints + Business logic + Exception handling

## Project Documentation

- `REQUIREMENTS.md` - Complete functional specifications
- `DEVELOPMENT_PLAN.md` - 6-package implementation roadmap
- `PHASE_1_DETAILED.md` - Granular implementation tasks
- `PROJECT_CONTEXT.md` - Current status and next actions