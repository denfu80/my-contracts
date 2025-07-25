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

**📍 Current Status**: Package 1 OFFICIALLY COMPLETE ✅  
**🎯 Next Package**: Package 2 - LLM Service Abstraction Layer  
**📋 Current progress**: See `DEVELOPMENT_PLAN.md` for package roadmap and next steps

### Package 1 Completion Summary
✅ **Infrastructure**: Git-based production deployment on Proxmox Docker node (192.168.4.8)  
✅ **Services**: Spring Boot API, PostgreSQL, Redis all running and healthy  
✅ **Deployment**: Automated scripts for updates and backups  
✅ **Production URL**: http://192.168.4.8:3000/api/v1/health responding correctly

**📚 Complete architecture details**: See `REQUIREMENTS.md` for technical requirements  
**🗂️ Technology stack decisions**: See `DEVELOPMENT_PLAN.md` for complete tech stack breakdown


## Development Commands

**📋 Complete development commands**: See `README.md` for current quick start guide  
**⚡ Production deployment**: See production section in `README.md` for deployment procedures

### Quick Reference (Package 1 Complete)
```bash
# Local development
docker-compose up -d  # Production services
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d  # With dev tools

# Production (on 192.168.4.8)
cd /opt/docmgr
./scripts/deploy.sh   # Update deployment
./scripts/backup.sh   # Backup database

# Health checks
curl http://localhost:3000/api/v1/health     # Local
curl http://192.168.4.8:3000/api/v1/health   # Production
```

## Architecture Patterns & Design Decisions

**📚 Complete architecture patterns**: See `REQUIREMENTS.md` for detailed technical architecture  
**🏗️ System design decisions**: See `DEVELOPMENT_PLAN.md` for technology choices and patterns

### Current Implementation (Package 1 Complete)
- **Production deployment**: Running on Proxmox Docker node with git-based updates
- **Containerized services**: PostgreSQL + Redis + Spring Boot API with health checks
- **Automation**: Deployment scripts, backup automation, container restart policies
- **Development tools**: pgAdmin + Redis Commander available in dev mode

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

### Current Package Status (PRODUCTION-FIRST APPROACH)
- **Package 1**: ✅ OFFICIALLY COMPLETE ✅ (Infrastructure + Production Deployment)
- **Package 2**: 🎯 READY TO START (LLM Service Abstraction Layer - 2-3 weeks)
- **Package 3**: 🔲 PENDING (Document Upload & Storage)
- **Package 4**: 🔲 PENDING (Mobile-First Web Interface)
- **Package 5**: 🔲 PENDING (LLM Document Processing)

### Package Scope Reminders (PRODUCTION-FIRST APPROACH)
- **Package 1**: ✅ Git-based production deployment on existing Docker infrastructure
- **Package 2**: 🎯 LLM Service Abstraction (Gemini + Ollama providers with Spring)
- **Package 3**: 🔲 Document Upload & Storage (File handling + database schema)
- **Package 4**: 🔲 Mobile-First Web Interface (React PWA + Android share)
- **Package 5**: 🔲 LLM Document Processing (Analysis pipeline + entity extraction)

## Project Documentation

- `REQUIREMENTS.md` - Complete functional specifications
- `DEVELOPMENT_PLAN.md` - 6-package implementation roadmap
- `README.md` - Current status with production deployment instructions