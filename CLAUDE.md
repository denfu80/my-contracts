# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Documentation Hierarchy & References

### 📋 **CRITICAL: Single Source of Truth**
- **`REQUIREMENTS.md`**: THE foundation document - all project requirements, features, and technical specifications
- **`DEVELOPMENT_PLAN.md`**: High-level 6-package development strategy with timelines  
- **`PHASE_1_DETAILED.md`**: Granular implementation tasks, file structures, and success criteria
- **`README.md`**: Current project status and quick start instructions

### 🔄 **Documentation Discipline Rules**
- **REQUIREMENTS.md is the single source of truth** for all features and technical specifications
- **Never duplicate information across files** - use references instead
- **Each document has a specific role** in the hierarchy  
- **When updating features**: Update REQUIREMENTS.md first, then reference it
- **Keep documents focused** on their specific purpose only

## Project Overview

Personal Document Management Service - a self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology.

**📚 Complete project details**: See `REQUIREMENTS.md`  
**📋 Development strategy**: See `DEVELOPMENT_PLAN.md`  
**⚡ Current implementation tasks**: See `PHASE_1_DETAILED.md`

## Current Status & Next Actions

**📍 Current Status**: Phase 1.1 OFFICIALLY COMPLETE ✅  
**🎯 Next Phase**: Phase 1.2 - Git-Based Production Deployment (SIMPLIFIED - 1-2 hours)  
**📋 Detailed status**: See `PHASE_1_DETAILED.md` for verification checklists and next steps

### Phase 1.1 Completion Summary
✅ **Delivered**: Environment setup, basic Docker foundation, Spring Boot API with health endpoint  
✅ **Verified Working**: All services start via `docker-compose up -d`, health endpoint responds  
✅ **Production Ready**: Current system ready for Phase 1.2 infrastructure deployment

**📚 Complete architecture details**: See `REQUIREMENTS.md` for technical requirements  
**🗂️ Technology stack decisions**: See `DEVELOPMENT_PLAN.md` for complete tech stack breakdown


## Development Commands

**📋 Complete development commands**: See `README.md` for current quick start guide  
**⚡ Detailed build/test commands**: See `PHASE_1_DETAILED.md` for comprehensive command reference

### Quick Reference (Current Phase 1.1)
```bash
# Start production services
docker-compose up -d

# Start development environment (with pgAdmin, Redis Commander)
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Health check
curl http://localhost:3000/api/v1/health
```

## Architecture Patterns & Design Decisions

**📚 Complete architecture patterns**: See `REQUIREMENTS.md` for detailed technical architecture  
**🏗️ System design decisions**: See `DEVELOPMENT_PLAN.md` for technology choices and patterns

### Current Implementation (Phase 1.1)
- **Containerized services**: PostgreSQL + Redis + Spring Boot API
- **Health monitoring**: Built-in health checks and service discovery
- **Development tools**: pgAdmin + Redis Commander for local development

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

### Current Phase Status (PRODUCTION-FIRST APPROACH - SIMPLIFIED)
- **Phase 1.1**: ✅ OFFICIALLY COMPLETE ✅ (Environment + Basic Docker + API)
- **Phase 1.2**: 🎯 READY TO START (Git-Based Production Deployment - 1-2 hours)
- **Phase 1.3**: 🔲 PENDING (Development Tools & Advanced Docker)

### Phase Scope Reminders (PRODUCTION-FIRST - SIMPLIFIED)
- **Phase 1.1**: ✅ Project structure + Basic Spring Boot + Core containers
- **Phase 1.2**: 🎯 Git-based deployment to existing Proxmox Docker node (NO Terraform/Ansible)
- **Phase 1.3**: 🔲 Development tools + Advanced Docker features
- **Package 2**: 🔲 Document Upload & Storage (extends production deployment)
- **Package 3**: 🔲 LLM Service Abstraction (extends production deployment)

## Project Documentation

- `REQUIREMENTS.md` - Complete functional specifications
- `DEVELOPMENT_PLAN.md` - 6-package implementation roadmap
- `PHASE_1_DETAILED.md` - Granular implementation tasks
- `PHASE_1_2_IMPLEMENTATION.md` - Git-based production deployment guide
- `README.md` - Current status with production deployment instructions