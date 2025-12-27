package com.apm.platform.application.dto.response;

import java.time.Instant;
import java.util.Map;

public class MetricResponse {
    private final String id;
    private final String systemId;
    private final long latencyMs;
    private final int statusCode;
    private final boolean hasError;
    private final double cpuUsagePercent;
    private final double memoryUsagePercent;
    private final Map<String, Object> additionalData;
    private final Instant collectedAt;

    public MetricResponse(String id, String systemId, long latencyMs, int statusCode,
                         boolean hasError, double cpuUsagePercent, double memoryUsagePercent,
                         Map<String, Object> additionalData, Instant collectedAt) {
        this.id = id;
        this.systemId = systemId;
        this.latencyMs = latencyMs;
        this.statusCode = statusCode;
        this.hasError = hasError;
        this.cpuUsagePercent = cpuUsagePercent;
        this.memoryUsagePercent = memoryUsagePercent;
        this.additionalData = additionalData;
        this.collectedAt = collectedAt;
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

    public boolean isHasError() {
        return hasError;
    }

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public double getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public Instant getCollectedAt() {
        return collectedAt;
    }
}
