#!/bin/bash
set -e

echo "🗄️ Starting database backup..."

# Configuration
PROJECT_DIR="/opt/docmgr"
BACKUP_DIR="$PROJECT_DIR/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="db_backup_$TIMESTAMP.sql"

# Navigate to project directory
cd "$PROJECT_DIR"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

echo "📦 Creating PostgreSQL backup..."
# Create database backup using existing container name
docker-compose exec -T database pg_dump -U docmgr_user docmgr > "$BACKUP_DIR/$BACKUP_FILE"

# Compress the backup
echo "🗜️ Compressing backup..."
gzip "$BACKUP_DIR/$BACKUP_FILE"

# Verify backup was created
if [ -f "$BACKUP_DIR/$BACKUP_FILE.gz" ]; then
    BACKUP_SIZE=$(du -h "$BACKUP_DIR/$BACKUP_FILE.gz" | cut -f1)
    echo "✅ Backup created: $BACKUP_FILE.gz ($BACKUP_SIZE)"
else
    echo "❌ Backup failed!"
    exit 1
fi

# Clean up old backups (keep 7 days)
echo "🧹 Cleaning up old backups (keeping 7 days)..."
find "$BACKUP_DIR" -name "db_backup_*.sql.gz" -mtime +7 -delete

# Show current backups
echo "📋 Current backups:"
ls -lh "$BACKUP_DIR"/db_backup_*.sql.gz 2>/dev/null || echo "No backups found"

echo "🎉 Backup completed successfully!"