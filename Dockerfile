# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY apm-domain/pom.xml apm-domain/
COPY apm-application/pom.xml apm-application/
COPY apm-infrastructure/pom.xml apm-infrastructure/
COPY apm-interface/pom.xml apm-interface/
COPY apm-starter/pom.xml apm-starter/

RUN mvn dependency:go-offline -B

COPY apm-domain/src apm-domain/src
COPY apm-application/src apm-application/src
COPY apm-infrastructure/src apm-infrastructure/src
COPY apm-interface/src apm-interface/src
COPY apm-starter/src apm-starter/src

RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="APM Platform"
LABEL description="Application Performance Monitoring Platform"

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /build/apm-starter/target/apm-starter-*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
