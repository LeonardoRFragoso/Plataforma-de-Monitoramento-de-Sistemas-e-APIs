package com.apm.platform.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

public final class MetricSnapshot {
    private final long latencyMs;
    private final double cpuUsagePercent;
    private final double memoryUsagePercent;
    private final int statusCode;
    private final boolean hasError;
    private final Instant timestamp;

    private MetricSnapshot(long latencyMs, double cpuUsagePercent, double memoryUsagePercent, 
                          int statusCode, boolean hasError, Instant timestamp) {
        this.latencyMs = latencyMs;
        this.cpuUsagePercent = cpuUsagePercent;
        this.memoryUsagePercent = memoryUsagePercent;
        this.statusCode = statusCode;
        this.hasError = hasError;
        this.timestamp = timestamp;
    }

    public static MetricSnapshot create(long latencyMs, double cpuUsagePercent, double memoryUsagePercent, 
                                       int statusCode, boolean hasError) {
        if (latencyMs < 0) {
            throw new IllegalArgumentException("Latency cannot be negative");
        }
        if (cpuUsagePercent < 0 || cpuUsagePercent > 100) {
            throw new IllegalArgumentException("CPU usage must be between 0 and 100");
        }
        if (memoryUsagePercent < 0 || memoryUsagePercent > 100) {
            throw new IllegalArgumentException("Memory usage must be between 0 and 100");
        }
        return new MetricSnapshot(latencyMs, cpuUsagePercent, memoryUsagePercent, statusCode, hasError, Instant.now());
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public double getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean hasError() {
        return hasError;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isSuccessfulResponse() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean isHighLatency(long thresholdMs) {
        return latencyMs > thresholdMs;
    }

    public boolean isHighCpuUsage(double thresholdPercent) {
        return cpuUsagePercent > thresholdPercent;
    }

    public boolean isHighMemoryUsage(double thresholdPercent) {
        return memoryUsagePercent > thresholdPercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricSnapshot that = (MetricSnapshot) o;
        return latencyMs == that.latencyMs &&
                Double.compare(that.cpuUsagePercent, cpuUsagePercent) == 0 &&
                Double.compare(that.memoryUsagePercent, memoryUsagePercent) == 0 &&
                statusCode == that.statusCode &&
                hasError == that.hasError &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latencyMs, cpuUsagePercent, memoryUsagePercent, statusCode, hasError, timestamp);
    }

    @Override
    public String toString() {
        return "MetricSnapshot{" +
                "latencyMs=" + latencyMs +
                ", cpuUsagePercent=" + cpuUsagePercent +
                ", memoryUsagePercent=" + memoryUsagePercent +
                ", statusCode=" + statusCode +
                ", hasError=" + hasError +
                ", timestamp=" + timestamp +
                '}';
    }
}
