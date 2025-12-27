FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="APM Platform"
LABEL description="Application Performance Monitoring Platform"

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY apm-starter/target/apm-starter-*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
