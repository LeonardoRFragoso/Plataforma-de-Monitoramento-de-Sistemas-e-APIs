package com.apm.platform.domain.entity;

import com.apm.platform.domain.exception.InvalidMetricDataException;
import com.apm.platform.domain.valueobject.MetricSnapshot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Metric {
    private final String id;
    private final String systemId;
    private final long latencyMs;
    private final int statusCode;
    private final boolean hasError;
    private final double cpuUsagePercent;
    private final double memoryUsagePercent;
    private final Map<String, Object> additionalData;
    private final Instant collectedAt;

    private Metric(String id, String systemId, long latencyMs, int statusCode, boolean hasError,
                  double cpuUsagePercent, double memoryUsagePercent, Map<String, Object> additionalData,
                  Instant collectedAt) {
        this.id = id;
        this.systemId = systemId;
        this.latencyMs = latencyMs;
        this.statusCode = statusCode;
        this.hasError = hasError;
        this.cpuUsagePercent = cpuUsagePercent;
        this.memoryUsagePercent = memoryUsagePercent;
        this.additionalData = new HashMap<>(additionalData);
        this.collectedAt = collectedAt;
    }

    public static Metric create(String systemId, long latencyMs, int statusCode, boolean hasError,
                               double cpuUsagePercent, double memoryUsagePercent) {
        return create(systemId, latencyMs, statusCode, hasError, cpuUsagePercent, memoryUsagePercent, new HashMap<>());
    }

    public static Metric create(String systemId, long latencyMs, int statusCode, boolean hasError,
                               double cpuUsagePercent, double memoryUsagePercent, Map<String, Object> additionalData) {
        validateSystemId(systemId);
        validateLatency(latencyMs);
        validatePercentage(cpuUsagePercent, "CPU usage");
        validatePercentage(memoryUsagePercent, "Memory usage");

        String id = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new Metric(id, systemId, latencyMs, statusCode, hasError, 
                         cpuUsagePercent, memoryUsagePercent, additionalData, now);
    }

    public static Metric fromSnapshot(String systemId, MetricSnapshot snapshot) {
        return create(systemId, snapshot.getLatencyMs(), snapshot.getStatusCode(), 
                     snapshot.hasError(), snapshot.getCpuUsagePercent(), 
                     snapshot.getMemoryUsagePercent());
    }

    public static Metric reconstitute(String id, String systemId, long latencyMs, int statusCode, 
                                     boolean hasError, double cpuUsagePercent, double memoryUsagePercent,
                                     Map<String, Object> additionalData, Instant collectedAt) {
        return new Metric(id, systemId, latencyMs, statusCode, hasError, 
                         cpuUsagePercent, memoryUsagePercent, additionalData, collectedAt);
    }

    public MetricSnapshot toSnapshot() {
        return MetricSnapshot.create(latencyMs, cpuUsagePercent, memoryUsagePercent, statusCode, hasError);
    }

    public boolean isSuccessful() {
        return !hasError && statusCode >= 200 && statusCode < 300;
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

    public boolean isServerError() {
        return statusCode >= 500;
    }

    public boolean isClientError() {
        return statusCode >= 400 && statusCode < 500;
    }

    public boolean indicatesDegradation(long latencyThresholdMs, double cpuThresholdPercent) {
        return isHighLatency(latencyThresholdMs) || isHighCpuUsage(cpuThresholdPercent) || hasError;
    }

    private static void validateSystemId(String systemId) {
        if (systemId == null || systemId.isBlank()) {
            throw new InvalidMetricDataException("System ID cannot be null or blank");
        }
    }

    private static void validateLatency(long latencyMs) {
        if (latencyMs < 0) {
            throw new InvalidMetricDataException("Latency cannot be negative");
        }
        if (latencyMs > 300000) {
            throw new InvalidMetricDataException("Latency exceeds maximum threshold (5 minutes)");
        }
    }

    private static void validatePercentage(double value, String fieldName) {
        if (value < 0 || value > 100) {
            throw new InvalidMetricDataException(fieldName + " must be between 0 and 100");
        }
    }

    public String getId() {
        return id;
    }

    public String getSystemId() {
        return systemId;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean hasError() {
        return hasError;
    }

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public double getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public Map<String, Object> getAdditionalData() {
        return new HashMap<>(additionalData);
    }

    public Instant getCollectedAt() {
        return collectedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metric metric = (Metric) o;
        return Objects.equals(id, metric.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Metric{" +
                "id='" + id + '\'' +
                ", systemId='" + systemId + '\'' +
                ", latencyMs=" + latencyMs +
                ", statusCode=" + statusCode +
                ", hasError=" + hasError +
                ", collectedAt=" + collectedAt +
                '}';
    }
}
