# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Personal Document Management Service - a self-hosted Java/Spring Boot application for automatically processing contracts, bills, and emails using LLM technology. Currently in pre-implementation planning phase.

## Architecture & Tech Stack

- **Backend**: Java 21 + Spring Boot 3.x + Spring Data JPA + PostgreSQL
- **Frontend**: React + TypeScript + Vite (PWA with mobile-first design)
- **LLM Integration**: Gemini Flash 1.5 API + Ollama (local) with strategy pattern
- **Document Processing**: Apache Tika + PDFBox + Tesseract OCR
- **Infrastructure**: Docker Compose + PostgreSQL + Redis

## Current Status

**Phase**: Pre-implementation (no source code exists yet)
**Next Action**: Package 1 - Initialize Spring Boot project with Gradle and Docker setup
**Implementation Strategy**: 6 incremental packages (see `DEVELOPMENT_PLAN.md`)

## Development Commands

> **Note**: These commands will be available after implementation begins

### Backend (Java/Spring Boot)
```bash
cd backend
./gradlew bootRun              # Start development server
./gradlew test                 # Run JUnit tests
./gradlew test --continuous    # Watch mode testing
./gradlew flywayMigrate        # Database migrations
./gradlew spotlessApply        # Auto-format code
```

### Frontend (React PWA)
```bash
cd frontend
npm run dev          # Start Vite dev server
npm run build        # Build production PWA
npm run test         # Run Vitest tests
npm run lint         # ESLint + TypeScript checking
```

### Docker Operations
```bash
docker-compose up -d              # Start all services
docker-compose down               # Stop all services
docker-compose logs -f [service]  # View service logs
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