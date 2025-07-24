# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Personal Document Management Service** - a self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology. The project is currently in the planning/requirements phase.

## Planned Architecture

### Technology Stack
- **Backend**: Node.js/TypeScript API server
- **Frontend**: Modern reactive UI (React/Vue with TypeScript)
- **Database**: PostgreSQL in Docker container
- **LLM Integration**: Local or API-based language model for document processing
- **Deployment**: Docker Compose multi-container setup

### Core Components (Planned)
- **Document Processing Pipeline**: File upload → LLM analysis → structured storage
- **Email Integration**: Forward emails for automatic processing
- **Dashboard**: Document insights, cost analysis, important date tracking
- **Search System**: Full-text search with AI-powered categorization
- **Data Management**: Local storage with backup/export capabilities

## Development Status

**Current State**: Requirements defined, ready for implementation
**Implementation Strategy**: 6 foundational packages building incrementally

## Development Packages (Phase 1)

### Package 1: Project Foundation & Docker Setup
- Docker Compose (Node.js API, PostgreSQL, Ollama)
- Basic TypeScript API with health endpoints
- Database schema and environment configuration
- **Success**: `docker-compose up` runs all services

### Package 2: LLM Service Abstraction Layer  
- Strategy pattern for LLM providers
- Gemini Flash 1.5 + Ollama implementations
- Rate limiting and token usage tracking
- **Success**: Switch between providers, process text requests

### Package 3: Document Upload & Storage
- File upload API with PDF text extraction
- Document metadata storage (PostgreSQL)
- Basic file validation and storage
- **Success**: Upload PDF, extract text, store metadata

### Package 4: Mobile-First Web Interface
- React PWA with mobile-first responsive design
- Android Share API integration
- File upload interface with drag-and-drop
- **Success**: Mobile/desktop compatibility, accepts shared files

### Package 5: Basic LLM Document Processing
- Document analysis pipeline (text → LLM → structured data)
- Entity extraction and categorization
- Results storage and insights display
- **Success**: End-to-end document processing with LLM analysis

### Package 6: Deployment & Infrastructure Automation
- Proxmox deployment with Terraform + Ansible
- Production Docker configuration with security hardening
- Monitoring, logging, and backup systems
- **Success**: One-command production deployment

## Technology Stack (Phase 1)
- **Backend**: Node.js + Express + TypeScript + Prisma ORM
- **Frontend**: React + TypeScript + Vite (PWA)
- **Database**: PostgreSQL
- **LLM**: Google GenAI SDK + Ollama REST API
- **PDF**: pdf-parse + tesseract (OCR)
- **Deployment**: Docker Compose

## Key Features to Implement

### Document Processing
- Web-based upload interface with drag-and-drop
- Email forwarding integration (IMAP or dedicated address)
- OCR and text extraction capabilities
- LLM-powered content analysis and entity extraction

### Organization & Search
- AI-powered document classification and tagging
- Full-text search with label-based filtering
- Document relationship mapping
- Advanced search with filters and sorting

### Dashboard & Analytics
- Financial and contractual insights overview
- Contract termination and payment due date alerts
- Cost analysis and visualization
- Activity feed for recent processing

## Security & Privacy Requirements

- **Local-First Approach**: All data processing happens locally
- **No External Data Transmission**: Complete local control
- **Containerized Deployment**: Docker setup for security isolation
- **HTTPS/TLS**: Secure web interface
- **Access Control**: Authentication system

## Data Flow (Planned)

1. **Document Ingestion**: Upload/email → format detection → text extraction
2. **LLM Analysis**: Content analysis → entity extraction → classification → tagging
3. **Storage & Indexing**: Structured DB storage → full-text indexing → file storage
4. **Retrieval**: Search interface → filtered results → document preview

## Development Commands (Post-Implementation)

**Current Status**: No package.json exists yet - these commands will be available after Package 1 completion.

### Backend (Node.js/TypeScript)
```bash
# After Package 1 implementation:
cd backend
npm run dev          # Start development server with hot reload
npm run build        # Build for production
npm run test         # Run Jest tests
npm run test:watch   # Run tests in watch mode
npm run lint         # ESLint code checking
npm run lint:fix     # Auto-fix linting issues
npm run db:migrate   # Run Prisma migrations
npm run db:seed      # Seed development database
npm run db:studio    # Open Prisma Studio
```

### Frontend (React PWA)
```bash
# After Package 4 implementation:
cd frontend
npm run dev          # Start Vite dev server
npm run build        # Build PWA for production
npm run preview      # Preview production build
npm run test         # Run Vitest tests
npm run test:ui      # Run tests with UI
npm run lint         # ESLint + TypeScript checking
```

### Docker & Infrastructure
```bash
# Development environment
docker-compose up -d              # Start all services
docker-compose down               # Stop all services
docker-compose logs -f [service]  # View service logs
docker-compose exec api bash      # Shell into API container

# Production deployment (Package 6)
cd infrastructure/terraform
terraform plan                    # Plan infrastructure changes
terraform apply                   # Deploy to Proxmox
cd ../ansible
ansible-playbook deploy.yml       # Configure services
```

### Full Stack Operations
```bash
# After full implementation:
npm run start:all    # Start all services in development
npm run test:e2e     # Run end-to-end tests with Playwright
npm run deploy:prod  # Deploy to production (Package 6)
```

## Project Status & Next Steps

**Current Phase**: Ready for Package 1 implementation
**Next Action**: Initialize Node.js projects and Docker configuration

Refer to `PHASE_1_DETAILED.md` for specific implementation tasks and file structures.

## Development Considerations

- Prioritize data privacy and local processing
- Design for easy Docker deployment and configuration
- Plan for high document processing accuracy (95%+ target)
- Ensure sub-second search response times
- Build intuitive UI requiring minimal user training
- Use Context7 MCP for latest API documentation
- TypeScript strict mode with comprehensive testing
- Mobile-first responsive design principles