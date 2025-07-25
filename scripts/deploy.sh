#!/bin/bash
set -e

echo "🚀 Deploying Document Management Service..."

# Configuration
PROJECT_DIR="/opt/docmgr"

# Navigate to project directory
cd "$PROJECT_DIR"

echo "📦 Pulling latest changes from git..."
git pull origin main

echo "🔨 Building and starting containers..."
docker-compose up -d --build

echo "⏳ Waiting for services to start..."
sleep 20

echo "🔍 Checking service health..."

# Check API health
if curl -f http://localhost:3000/api/v1/health > /dev/null 2>&1; then
    echo "✅ API is healthy"
else
    echo "❌ API health check failed"
    echo "📋 Container status:"
    docker-compose ps
    echo "📋 API logs:"
    docker-compose logs --tail 20 api
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
echo "📍 Application URL: http://192.168.4.8:3000/api/v1/health"

# Show current status
echo "📊 Current container status:"
docker-compose ps