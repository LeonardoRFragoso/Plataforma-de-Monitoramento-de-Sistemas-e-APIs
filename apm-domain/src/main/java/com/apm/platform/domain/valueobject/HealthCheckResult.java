package com.apm.platform.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

public final class HealthCheckResult {
    private final SystemStatus status;
    private final long responseTimeMs;
    private final int statusCode;
    private final String message;
    private final Instant timestamp;

    private HealthCheckResult(SystemStatus status, long responseTimeMs, int statusCode, String message, Instant timestamp) {
        this.status = status;
        this.responseTimeMs = responseTimeMs;
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static HealthCheckResult create(SystemStatus status, long responseTimeMs, int statusCode, String message) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (responseTimeMs < 0) {
            throw new IllegalArgumentException("Response time cannot be negative");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank");
        }
        return new HealthCheckResult(status, responseTimeMs, statusCode, message, Instant.now());
    }

    public static HealthCheckResult healthy(long responseTimeMs, int statusCode) {
        return create(SystemStatus.UP, responseTimeMs, statusCode, "Health check successful");
    }

    public static HealthCheckResult degraded(long responseTimeMs, int statusCode, String reason) {
        return create(SystemStatus.DEGRADED, responseTimeMs, statusCode, reason);
    }

    public static HealthCheckResult down(String reason) {
        return create(SystemStatus.DOWN, 0, 0, reason);
    }

    public SystemStatus getStatus() {
        return status;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isHealthy() {
        return status.isHealthy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthCheckResult that = (HealthCheckResult) o;
        return responseTimeMs == that.responseTimeMs &&
                statusCode == that.statusCode &&
                status == that.status &&
                Objects.equals(message, that.message) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, responseTimeMs, statusCode, message, timestamp);
    }

    @Override
    public String toString() {
        return "HealthCheckResult{" +
                "status=" + status +
                ", responseTimeMs=" + responseTimeMs +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
