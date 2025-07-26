# Personal Document Management Service

A self-hosted solution for automatically processing contracts, bills, and forwarded emails using LLM technology.

> **📋 Complete project details**: See [`REQUIREMENTS.md`](REQUIREMENTS.md) for full requirements and features  
> **🗂️ Development roadmap**: See [`DEVELOPMENT_PLAN.md`](DEVELOPMENT_PLAN.md) for complete development strategy

## 🎯 Current Status

**✅ Package 1 COMPLETE**: Production infrastructure with git-based deployment to 192.168.4.8  
**✅ Package 2 COMPLETE**: LLM Service Abstraction Layer with Gemini & Ollama providers  
**🎯 Next Package**: Package 3 - Document Processing Pipeline  
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

4. **Configure Environment (Important!)**
   ```bash
   # The .env file is automatically loaded by docker-compose
   # Edit .env file to configure your API keys and settings:
   nano .env
   
   # Key settings:
   # GEMINI_API_KEY=your_api_key_here
   # OLLAMA_BASE_URL=http://your_ollama_host:11434
   ```

5. **Access Services**
   - **API**: http://localhost:3000/api/v1/health
   - **LLM Health Dashboard**: http://localhost:3000/health-dashboard
   - **pgAdmin**: http://localhost:8080 (dev environment only)
   - **Redis Commander**: http://localhost:8081 (dev environment only)

### ✅ What's Working Now (Package 2 Complete)
- **Production deployment** on Proxmox Docker node (192.168.4.8:3000)
- **Spring Boot API** with health endpoint responding
- **✅ LLM Service Abstraction** with Gemini and Ollama provider support
- **✅ LLM REST API** endpoints for completion, analysis, and provider management
- **✅ Rate limiting** with Redis-backed provider rate limits
- **✅ Provider fallback** and health monitoring with auto-switching
- **✅ Health Dashboard** at `/health-dashboard` for monitoring LLM providers
- **PostgreSQL database** with secure production configuration
- **Redis cache** for performance and rate limiting (+ LLM rate limiting)
- **Git-based deployment** process with automated scripts
- **Development tools** (pgAdmin, Redis Commander) in dev mode with hot reload
- **Backup automation** with rotation and compression

### 🔧 Development Commands

#### **Local Development (Fastest - Recommended)**
```bash
# Start only dependencies
docker-compose up -d database redis

# Run application locally with hot reload
cd backend
./gradlew bootRun

# Access points:
# - API: http://localhost:3000/api/v1/health
# - LLM Health Dashboard: http://localhost:3000/health-dashboard
# - Debug port: 5005 (for IDE debugging)
```

#### **Docker Development with Hot Reload**
```bash
# Start development environment with automatic restart
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Features:
# - Spring Boot DevTools hot reload
# - JVM debug port 5005 exposed
# - pgAdmin: http://localhost:8080 (admin@example.com / admin123)
# - Redis Commander: http://localhost:8081
```

#### **Production Mode**
```bash
# Start production-like environment
docker-compose up -d

# Access:
# - API: http://localhost:3000/api/v1/health  
# - LLM Dashboard: http://localhost:3000/health-dashboard
```

#### **Build & Test**
```bash
# Build the application
cd backend
./gradlew build

# Run tests only
./gradlew test

# Clean build (removes previous build artifacts)
./gradlew clean build

# Check code formatting (Spotless)
./gradlew spotlessCheck

# Apply code formatting
./gradlew spotlessApply
```

#### **LLM API Testing (Package 2 Complete)**
```bash
# Health checks
curl http://localhost:3000/api/v1/health                # General API health
curl http://localhost:3000/api/v1/llm/health           # LLM provider health
curl http://localhost:3000/api/v1/llm/providers        # Available providers
curl http://localhost:3000/api/v1/llm/models           # Supported models

# Text completion
curl -X POST http://localhost:3000/api/v1/llm/complete \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Hello, how are you?", "maxTokens": 50}'

# Document analysis (structured extraction)
curl -X POST http://localhost:3000/api/v1/llm/analyze \
  -H "Content-Type: application/json" \
  -d '{"text": "Invoice #123 for $500", "documentType": "INVOICE"}'

# Provider management
curl -X POST http://localhost:3000/api/v1/llm/providers/ollama/activate
curl http://localhost:3000/api/v1/llm/providers/active
```

#### **Rebuild & Deploy Changes**
```bash
# After making code changes, rebuild and restart:
cd backend
./gradlew build

# Rebuild Docker containers with latest code:
docker-compose up -d --build

# Or force complete rebuild:
docker-compose down
docker-compose up -d --build
```

#### **Debugging Instructions**

##### **IntelliJ IDEA Setup**
```bash
# 1. Start application in debug mode
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
# OR for local development:
./gradlew bootRun

# 2. In IntelliJ: Run > Edit Configurations > Add > Remote JVM Debug
# Host: localhost
# Port: 5005
# Command line args: -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

# 3. Set breakpoints and click Debug
```

##### **VS Code Setup**
```bash
# Create .vscode/launch.json with:
```
```json
{
    "type": "java",
    "name": "Debug Spring Boot",
    "request": "attach",
    "hostName": "localhost",
    "port": 5005
}
```

##### **View Application Logs**
```bash
# Docker logs (live follow)
docker-compose logs -f api

# All services logs
docker-compose logs -f

# Specific timeframe
docker-compose logs --since="1h" api

# Last 100 lines
docker-compose logs --tail=100 api
```

##### **Environment Variable Debugging**
```bash
# Check what environment variables are loaded
docker exec -it docmgr-api env | grep -E "(GEMINI|OLLAMA|POSTGRES)"

# Check Spring Boot configuration
curl http://localhost:3000/actuator/env
# Note: Only works if actuator endpoints are enabled
```

#### **Database & Cache Access**
```bash
# PostgreSQL access
docker exec -it docmgr-postgres psql -U docmgr_user -d docmgr

# Redis access  
docker exec -it docmgr-redis redis-cli

# Check Redis keys for LLM data
docker exec -it docmgr-redis redis-cli keys "*llm*"
docker exec -it docmgr-redis redis-cli keys "*rate_limit*"
```

## 🔧 **Environment Configuration (.env File)**

### **How .env Works with Docker**

The `.env` file is **automatically loaded by docker-compose** and injects environment variables into containers:

```bash
# 1. Docker Compose reads .env file automatically
# 2. Variables are passed to containers via docker-compose.yml
# 3. Spring Boot reads them as environment variables
# 4. Application.properties uses ${VARIABLE_NAME:default} syntax
```

### **Verification Process**

```bash
# 1. Check if .env file exists and has correct values
cat .env | grep -E "(GEMINI|OLLAMA)"

# 2. Verify Docker containers receive the variables
docker exec -it docmgr-api env | grep -E "(GEMINI|OLLAMA)"

# 3. Check Spring Boot actually uses these values
curl http://localhost:3000/api/v1/llm/health
```

### **Environment Variable Flow**

```
.env file → docker-compose.yml → Container Environment → Spring Boot → Application
```

**Example:**
```bash
# In .env file:
OLLAMA_BASE_URL=http://192.168.4.5:11434

# In docker-compose.yml:
- OLLAMA_BASE_URL=${OLLAMA_BASE_URL:-http://ollama:11434}

# In application.properties:
app.llm.ollama.base-url=${OLLAMA_BASE_URL:http://ollama:11434}

# Result: Application uses http://192.168.4.5:11434
```

### **Troubleshooting .env Issues**

```bash
# Problem: Changes not taking effect
# Solution: Restart containers to reload environment
docker-compose down && docker-compose up -d

# Problem: Variables not found
# Check: docker-compose.yml must reference the variable
- VARIABLE_NAME=${VARIABLE_NAME:-default_value}

# Problem: .env file ignored
# Check: .env must be in same directory as docker-compose.yml
ls -la .env docker-compose.yml
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