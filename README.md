# Personal Document Management Service

A self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology.

> **📋 Complete project details**: See [`REQUIREMENTS.md`](REQUIREMENTS.md) for full requirements and features  
> **🗂️ Development roadmap**: See [`DEVELOPMENT_PLAN.md`](DEVELOPMENT_PLAN.md) for complete development strategy

## 🎯 Current Status

**✅ Package 1 COMPLETE**: Production infrastructure with git-based deployment to 192.168.4.8  
**🎯 Next Package**: Package 2 - LLM Service Abstraction Layer  
**📋 Development roadmap**: See [`DEVELOPMENT_PLAN.md`](DEVELOPMENT_PLAN.md) for complete package strategy

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

### ✅ What's Working Now (Package 1 Complete)
- **Production deployment** on Proxmox Docker node (192.168.4.8:3000)
- **Spring Boot API** with health endpoint responding
- **PostgreSQL database** with secure production configuration
- **Redis cache** for performance and rate limiting
- **Git-based deployment** process with automated scripts
- **Development tools** (pgAdmin, Redis Commander) in dev mode
- **Backup automation** with rotation and compression

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

## 🚀 Production Deployment ✅ ACTIVE

### Current Production Environment

**Status**: ✅ Running on Proxmox Docker node  
**URL**: http://192.168.4.8:3000/api/v1/health  
**Location**: `/opt/docmgr` on 192.168.4.8

### Update Process
```bash
# On Proxmox Docker node (192.168.4.8)
cd /opt/docmgr
./scripts/deploy.sh  # Automated: git pull + restart + health check
```

### Backup Process
```bash
# On Proxmox Docker node
./scripts/backup.sh  # PostgreSQL dump + compression + rotation
```

### Production Services
- **API**: Spring Boot on port 3000 ✅
- **Database**: PostgreSQL with secure password ✅  
- **Cache**: Redis for rate limiting ✅
- **Monitoring**: Container health checks ✅

## 📚 Complete Documentation

- **[`REQUIREMENTS.md`](REQUIREMENTS.md)** - Complete project requirements and technical specifications
- **[`DEVELOPMENT_PLAN.md`](DEVELOPMENT_PLAN.md)** - 6-package implementation roadmap with technology decisions
- **[`CLAUDE.md`](CLAUDE.md)** - Development guidance and package discipline for Claude Code