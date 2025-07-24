# Multi-stage build for production
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./

# Copy source code
COPY src src

# Build the application
RUN chmod +x gradlew && ./gradlew build -x test --no-daemon

# Production stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S docmgr && \
    adduser -S docmgr -u 1001 -G docmgr

# Create directories for uploads and temp files
RUN mkdir -p /app/uploads /app/temp && \
    chown -R docmgr:docmgr /app

# Copy built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Switch to non-root user
USER docmgr

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:3000/api/v1/health || exit 1

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "app.jar"]