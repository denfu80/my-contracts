# Personal Document Management Service

A self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology.

> **📋 Complete project details**: See [`REQUIREMENTS.md`](REQUIREMENTS.md) for full requirements and features  
> **🗂️ Development roadmap**: See [`DEVELOPMENT_PLAN.md`](DEVELOPMENT_PLAN.md) for complete development strategy

## 🎯 Current Status

**✅ Phase 1.1 COMPLETE**: Environment setup, basic Docker foundation, Spring Boot API with health endpoint  
**🎯 Next Phase**: Phase 1.2 - Git-Based Production Deployment (1-2 hours)  
**📋 Detailed progress**: See [`PHASE_1_DETAILED.md`](PHASE_1_DETAILED.md) for verification checklists

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

4. **Access Current Services**
   - **API**: http://localhost:3000/api/v1/health
   - **pgAdmin**: http://localhost:8080 (dev environment only)
   - **Redis Commander**: http://localhost:8081 (dev environment only)

### ✅ What's Working Now (Phase 1.1)
- **Spring Boot API** with health endpoint
- **PostgreSQL database** with health checks
- **Redis cache** for future session/rate limiting
- **Development tools** (pgAdmin, Redis Commander)
- **Docker containerization** with production and development modes

### 🔧 Development Commands
```bash
# Backend development
cd backend
./gradlew test          # Run tests
./gradlew bootRun       # Start locally (requires services)

# Database access
docker exec -it docmgr-postgres psql -U docmgr_user -d docmgr

# Redis access  
docker exec -it docmgr-redis redis-cli
```

## 🚀 Production Deployment

### Deploy to Existing Proxmox Docker Node

For production deployment to your existing Proxmox Docker node:

1. **See detailed guide**: [`PHASE_1_2_IMPLEMENTATION.md`](PHASE_1_2_IMPLEMENTATION.md)

2. **Quick deployment**:
   ```bash
   # On your Proxmox Docker node
   git clone <repository-url> /opt/docmgr
   cd /opt/docmgr
   cp .env.production .env  # Edit with your settings
   docker-compose -f docker-compose.prod.yml up -d
   ```

3. **Update process**:
   ```bash
   # For updates
   cd /opt/docmgr
   git pull origin main
   docker-compose -f docker-compose.prod.yml up -d --build
   ```

4. **Production access**:
   - **API**: `http://your-docker-node:3000/api/v1/health`
   - **Application**: `http://your-docker-node:3000`

### Automation Options
- **Manual updates**: Git pull + container restart
- **Cron job**: Scheduled automatic updates
- **Webhook**: Trigger updates on git push
- **CI/CD**: GitHub Actions deployment

## 📚 Complete Documentation

- **[`REQUIREMENTS.md`](REQUIREMENTS.md)** - Complete project requirements and technical specifications
- **[`DEVELOPMENT_PLAN.md`](DEVELOPMENT_PLAN.md)** - 6-package implementation roadmap with technology decisions
- **[`PHASE_1_DETAILED.md`](PHASE_1_DETAILED.md)** - Granular implementation tasks and verification checklists
- **[`PHASE_1_2_IMPLEMENTATION.md`](PHASE_1_2_IMPLEMENTATION.md)** - Git-based production deployment guide
- **[`CLAUDE.md`](CLAUDE.md)** - Development guidance and phase discipline for Claude Code