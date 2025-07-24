# Project Context for Claude Code Sessions

## Project Overview
I'm building a **Personal Document Management Service** - a self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology.

## Project Documentation Structure
- **`REQUIREMENTS.md`**: Complete project requirements, features, and technical specifications
- **`DEVELOPMENT_PLAN.md`**: High-level 6-package development strategy with timelines
- **`PHASE_1_DETAILED.md`**: Granular implementation tasks, file structures, and success criteria
- **`CLAUDE.md`**: Development guidance and architectural decisions for Claude Code

## Current Status
**Ready for Package 1 implementation** - All planning and requirements documentation complete.

## Key Architecture Decisions
- **LLM Providers**: Gemini Flash 1.5 (free tier) + Ollama (local) with Strategy pattern
- **Tech Stack**: Node.js/TypeScript + React PWA + PostgreSQL + Docker
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
Start with **Package 1: Project Foundation & Docker Setup** following detailed tasks in `PHASE_1_DETAILED.md`.

*Refer to the documentation files above for complete technical details and implementation specifics.*