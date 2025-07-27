#!/bin/bash
set -e

echo "🚀 Deploying Document Management Service..."

# Configuration
PROJECT_DIR="/opt/docmgr"
BACKUP_DIR="/opt/docmgr/backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Navigate to project directory
cd "$PROJECT_DIR"

# Create backup of current state
echo "💾 Creating backup of current deployment..."
mkdir -p "$BACKUP_DIR"
docker-compose ps > "$BACKUP_DIR/containers_${TIMESTAMP}.txt" 2>/dev/null || echo "No containers running"

echo "📦 Pulling latest changes from git..."
git pull origin main

echo "🛑 Stopping existing containers..."
docker-compose down

echo "🔨 Building and starting containers..."
if ! docker-compose up -d --build; then
    echo "❌ Failed to start containers"
    echo "📋 Attempting to restore previous state..."
    docker-compose down
    exit 1
fi

echo "⏳ Waiting for services to start..."
sleep 30

echo "🔍 Checking service health..."

# Function to perform health checks with retries
check_api_health() {
    local retries=6
    local wait_time=10
    
    for i in $(seq 1 $retries); do
        echo "🔍 API health check attempt $i/$retries..."
        if curl -f http://localhost:3000/api/v1/health > /dev/null 2>&1; then
            echo "✅ API is healthy"
            return 0
        fi
        
        if [ $i -lt $retries ]; then
            echo "⏳ API not ready yet, waiting ${wait_time}s..."
            sleep $wait_time
        fi
    done
    
    echo "❌ API health check failed after $retries attempts"
    echo "📋 Container status:"
    docker-compose ps
    echo "📋 API logs:"
    docker-compose logs --tail 30 api
    return 1
}

# Check API health with retries
if ! check_api_health; then
    echo "💥 Deployment failed - API not healthy"
    exit 1
fi

# Check database
if docker-compose exec -T database pg_isready -U docmgr_user -d docmgr > /dev/null 2>&1; then
    echo "✅ Database is healthy"
else
    echo "❌ Database health check failed"
    docker-compose logs --tail 10 database
    exit 1
fi

# Check Redis
if docker-compose exec -T redis redis-cli ping > /dev/null 2>&1; then
    echo "✅ Redis is healthy"
else
    echo "❌ Redis health check failed"
    docker-compose logs --tail 10 redis
    exit 1
fi

echo "🎉 Deployment completed successfully!"
echo "📍 Application URLs:"
echo "   - API Health: http://192.168.4.8:3000/api/v1/health"
echo "   - Swagger UI: http://192.168.4.8:3000/swagger-ui/index.html"
echo "   - LLM Health: http://192.168.4.8:3000/api/v1/llm/health"

# Show current status
echo "📊 Current container status:"
docker-compose ps

echo "💾 Deployment backup saved: $BACKUP_DIR/containers_${TIMESTAMP}.txt"
echo "🕐 Deployment completed at: $(date)"