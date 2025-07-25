# Phase 1.2 Implementation Guide - Git-Based Deployment

> **📋 Reference**: See [`REQUIREMENTS.md`](REQUIREMENTS.md) for complete project requirements  
> **🗂️ Phase Context**: See [`PHASE_1_DETAILED.md`](PHASE_1_DETAILED.md) for full phase breakdown

## Overview

Phase 1.2 deploys the working development environment from Phase 1.1 to your existing Proxmox Docker node using a simple git-based approach. This provides production deployment without complex infrastructure provisioning.

## Prerequisites

- Existing Proxmox Docker node with Docker and Docker Compose installed
- SSH access to the Docker node
- Git installed on the Docker node
- Network access from your intranet to the Docker node

## Deployment Strategy

### Git-Based Deployment Workflow
```
Local Development → Git Push → Proxmox Docker Node → Git Pull → Container Restart
```

### Advantages
- **Simple**: No complex infrastructure as code
- **Flexible**: Manual updates or automated via cron/webhook
- **Reliable**: Git provides version control and rollback capability
- **Scalable**: Easy to add automation later

## Implementation Steps

### Step 1: Prepare Production Configuration (30 minutes)

#### Create Production Docker Compose
The production configuration removes development tools and optimizes for production use:

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  api:
    build:
      context: ./backend
      dockerfile: Dockerfile
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DATABASE_URL=jdbc:postgresql://database:5432/docmgr
      - DATABASE_USERNAME=docmgr_user
      - DATABASE_PASSWORD=${DB_PASSWORD}
      - REDIS_URL=redis://redis:6379
    ports:
      - "3000:8080"
    depends_on:
      - database
      - redis
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/v1/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  database:
    image: postgres:15
    environment:
      POSTGRES_DB: docmgr
      POSTGRES_USER: docmgr_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./backups:/backups
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U docmgr_user -d docmgr"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    volumes:
      - redis_data:/data
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 3

volumes:
  postgres_data:
  redis_data:
```

#### Create Production Environment Template
```bash
# .env.production (template)
# Copy to .env on production server and fill in values

# Database
DB_PASSWORD=your_secure_database_password

# Application
SPRING_PROFILES_ACTIVE=production

# Optional: JVM settings
JAVA_OPTS=-Xmx2g -Xms1g
```

### Step 2: Setup on Proxmox Docker Node (30 minutes)

#### Initial Deployment
```bash
# SSH to your Proxmox Docker node
ssh user@your-docker-node

# Clone the repository
sudo mkdir -p /opt/docmgr
sudo chown $USER:$USER /opt/docmgr
cd /opt/docmgr
git clone <your-repository-url> .

# Create production environment file
cp .env.production .env
# Edit .env with your production values
nano .env

# Start production services
docker-compose -f docker-compose.prod.yml up -d

# Verify deployment
docker-compose -f docker-compose.prod.yml ps
curl http://localhost:3000/api/v1/health
```

### Step 3: Create Update Procedure (15 minutes)

#### Manual Update Process
```bash
# On Proxmox Docker node
cd /opt/docmgr

# Update from git
git pull origin main

# Rebuild and restart containers
docker-compose -f docker-compose.prod.yml up -d --build

# Verify health
docker-compose -f docker-compose.prod.yml ps
curl http://localhost:3000/api/v1/health
```

#### Optional: Simple Update Script
```bash
# scripts/deploy.sh (optional)
#!/bin/bash
set -e

echo "Updating Document Management Service..."

# Navigate to project directory
cd /opt/docmgr

# Pull latest changes
git pull origin main

# Rebuild and restart containers
docker-compose -f docker-compose.prod.yml up -d --build

# Wait for services to start
sleep 10

# Health check
if curl -f http://localhost:3000/api/v1/health > /dev/null 2>&1; then
    echo "✅ Deployment successful - API is healthy"
else
    echo "❌ Deployment failed - API health check failed"
    exit 1
fi

echo "🎉 Update completed successfully"
```

### Step 4: Basic Backup Solution (15 minutes)

#### Simple Database Backup
```bash
# scripts/backup.sh (optional)
#!/bin/bash

BACKUP_DIR="/opt/docmgr/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p $BACKUP_DIR

# Create PostgreSQL backup
docker exec docmgr-database-1 pg_dump -U docmgr_user docmgr > "$BACKUP_DIR/db_backup_$TIMESTAMP.sql"

# Compress backup
gzip "$BACKUP_DIR/db_backup_$TIMESTAMP.sql"

# Keep only last 7 days of backups
find "$BACKUP_DIR" -name "db_backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: db_backup_$TIMESTAMP.sql.gz"
```

#### Schedule Backups (Optional)
```bash
# Add to crontab on Proxmox Docker node
# Run backup daily at 2 AM
0 2 * * * /opt/docmgr/scripts/backup.sh
```

## Automation Options

### Option 1: Cron Job for Scheduled Updates
```bash
# Update every night at 3 AM (if you want automatic updates)
0 3 * * * cd /opt/docmgr && /opt/docmgr/scripts/deploy.sh
```

### Option 2: GitHub Webhook
Setup a simple webhook endpoint on your Docker node that triggers updates when you push to main branch.

### Option 3: GitHub Actions CI/CD
```yaml
# .github/workflows/deploy.yml (example)
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - name: Deploy to Docker Node
      uses: appleboy/ssh-action@v0.1.5
      with:
        host: ${{ secrets.DOCKER_NODE_HOST }}
        username: ${{ secrets.DOCKER_NODE_USER }}
        key: ${{ secrets.DOCKER_NODE_SSH_KEY }}
        script: |
          cd /opt/docmgr
          /opt/docmgr/scripts/deploy.sh
```

## Network Access

### Internal Access
- **API**: `http://your-docker-node:3000/api/v1/health`
- **Application**: `http://your-docker-node:3000` (when frontend is added)

### Optional: Reverse Proxy
If you want a custom domain or HTTPS, you can add a simple reverse proxy:
- Use your existing reverse proxy (if any)
- Or add Traefik/Nginx in front of the application later

## Monitoring & Maintenance

### Health Checks
```bash
# Check container status
docker-compose -f docker-compose.prod.yml ps

# Check logs
docker-compose -f docker-compose.prod.yml logs -f api

# Check API health
curl http://localhost:3000/api/v1/health

# Check database connection
docker exec -it docmgr-database-1 psql -U docmgr_user -d docmgr -c "SELECT 1;"
```

### Troubleshooting
```bash
# View container logs
docker-compose -f docker-compose.prod.yml logs [service_name]

# Restart specific service
docker-compose -f docker-compose.prod.yml restart api

# Rebuild containers
docker-compose -f docker-compose.prod.yml up -d --build

# Check resource usage
docker stats
```

## Rollback Procedure

### Quick Rollback
```bash
# If something goes wrong, rollback to previous version
cd /opt/docmgr
git log --oneline -5  # See recent commits
git checkout <previous_commit_hash>
docker-compose -f docker-compose.prod.yml up -d --build
```

### Full Restore
```bash
# Restore database from backup if needed
cd /opt/docmgr/backups
gunzip db_backup_YYYYMMDD_HHMMSS.sql.gz
docker exec -i docmgr-database-1 psql -U docmgr_user docmgr < db_backup_YYYYMMDD_HHMMSS.sql
```

## Security Considerations

### Basic Security
- Change default database password
- Use environment variables for sensitive data
- Ensure Docker node is properly secured
- Consider firewall rules to limit access
- Regular security updates for the Docker node

### Network Security
- Application runs on internal network only
- Database and Redis not exposed externally
- SSH access secured with key authentication

## Next Steps

### After Phase 1.2 Completion
1. **Package 2**: Document Upload & Storage
2. **Package 3**: LLM Service Abstraction Layer  
3. **Package 4**: Mobile-First Web Interface
4. **Package 5**: LLM Document Processing

Each subsequent package can be deployed using the same git-based approach:
```bash
# For each new package
git pull origin main
docker-compose -f docker-compose.prod.yml up -d --build
```

## Success Criteria

- ✅ Application running on Proxmox Docker node
- ✅ Accessible from your intranet
- ✅ Simple update process working
- ✅ Basic backup solution in place
- ✅ Health monitoring functional
- ✅ Ready for continuous development and deployment

This approach gives you production deployment in 1-2 hours instead of days, with the flexibility to add more automation as needed.