# Build stage
FROM eclipse-temurin:17-jdk AS builder

# Install required tools
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src ./src

# Build application
RUN ./gradlew clean build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre AS runtime

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user (handle existing UID/GID)
RUN groupadd -f -g 1001 docmgr && \
    useradd -u 1001 -g docmgr -s /bin/bash -m docmgr

# Set working directory
WORKDIR /app

# Create necessary directories
RUN mkdir -p /app/uploads /app/temp && \
    chown -R docmgr:docmgr /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown docmgr:docmgr app.jar

# Switch to non-root user
USER docmgr

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:3000/api/v1/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]