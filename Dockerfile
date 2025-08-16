# Multi-stage build for Spring Boot application
FROM gradle:8.14.3-jdk17-alpine AS builder

# Set working directory
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Copy source code
COPY src src

# Build the application
RUN gradle clean build -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-alpine AS runtime

# Add labels for metadata
LABEL maintainer="Atlas Team"
LABEL version="1.0"
LABEL description="Atlas Multi-tenant Application"

# Create non-root user for security
RUN addgroup -g 1001 atlas && \
    adduser -D -s /bin/sh -u 1001 -G atlas atlas

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown atlas:atlas app.jar

# Switch to non-root user
USER atlas

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Set JVM options for containerized environment
ENV JVM_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
