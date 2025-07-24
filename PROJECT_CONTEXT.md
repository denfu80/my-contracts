# Project Context for Claude Code Sessions

## Project Overview
I'm building a **Personal Document Management Service** - a self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology.

## Project Documentation Structure
- **`REQUIREMENTS.md`**: Complete project requirements, features, and technical specifications
- **`DEVELOPMENT_PLAN.md`**: High-level 6-package development strategy with timelines
- **`PHASE_1_DETAILED.md`**: Granular implementation tasks, file structures, and success criteria
- **`CLAUDE.md`**: Development guidance and architectural decisions for Claude Code

## Current Status
**Phase 1.1 OFFICIALLY COMPLETE ✅** - Environment setup, basic Docker foundation, and Spring Boot API with health endpoint implemented and verified.

**NEXT**: Phase 1.2 - Advanced Docker Features & Development Tools (READY TO START)

## Key Architecture Decisions
- **LLM Providers**: Gemini Flash 1.5 (free tier) + Ollama (local) with Strategy pattern
- **Tech Stack**: Java/Spring Boot + React PWA + PostgreSQL + Docker
- **Mobile-First**: Single responsive UI with Android Share API integration
- **Infrastructure**: Proxmox deployment with Terraform + Ansible automation

## Development Approach
**6 incremental packages**: Foundation → LLM Service → File Upload → Mobile UI → Processing → Deployment

Each package builds on the previous, with clear success criteria before advancing.

## Development Guidelines
- Use Context7 MCP for latest API documentation
- TypeScript strict mode with comprehensive testing
- Mobile-first responsive design principles
- Local-first architecture for data privacy

## Next Action
**Phase 1.1 VERIFIED AND COMPLETE** ✅ - Ready to proceed to **Phase 1.2: Advanced Docker Features & Development Tools** following updated tasks in `PHASE_1_DETAILED.md`.

## Phase 1.1 Completion Summary
✅ **Delivered**:
- Complete project structure with Git configuration
- Spring Boot API with health endpoint (/api/v1/health)
- Docker containerization (PostgreSQL + Redis + API)
- Environment configuration (.env.example, .env)
- Documentation updates (CLAUDE.md, README.md)

✅ **Verified Working**:
- All services start: `docker-compose up -d`
- Health check: http://localhost:3000/api/v1/health
- Database connectivity confirmed
- Redis connectivity confirmed

*Refer to the documentation files above for complete technical details and implementation specifics.*