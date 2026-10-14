# ─── Stage 1: Build with Maven ────────────────────────────────
# Use official Maven + JDK 17 image — Maven is pre-installed and on PATH
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml first so Docker can cache the dependency layer
COPY pom.xml .

# Pre-download all dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B -q

# Copy source code
COPY src ./src

# Build executable JAR (tests are run in CI pipeline, not during image build)
RUN mvn clean package -DskipTests -B -q

# ─── Stage 2: Minimal runtime image ───────────────────────────
# JRE-only image is smaller than JDK
FROM eclipse-temurin:17-jre-alpine

# Security: run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Copy the built JAR from builder stage
# The JAR name is finance-tracker-1.0.0.jar (matches artifactId + version in pom.xml)
COPY --from=builder /app/target/finance-tracker-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8080

# Health check — Docker marks the container unhealthy if /health fails
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/health || exit 1

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
