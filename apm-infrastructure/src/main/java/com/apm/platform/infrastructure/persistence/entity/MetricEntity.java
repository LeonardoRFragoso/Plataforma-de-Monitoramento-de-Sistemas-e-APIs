package com.apm.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "metrics", indexes = {
    @Index(name = "idx_metric_system_id", columnList = "system_id"),
    @Index(name = "idx_metric_collected_at", columnList = "collected_at"),
    @Index(name = "idx_metric_system_time", columnList = "system_id,collected_at")
})
public class MetricEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "system_id", nullable = false, length = 36)
    private String systemId;

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @Column(name = "has_error", nullable = false)
    private boolean hasError;

    @Column(name = "cpu_usage_percent", nullable = false)
    private double cpuUsagePercent;

    @Column(name = "memory_usage_percent", nullable = false)
    private double memoryUsagePercent;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "metric_additional_data", joinColumns = @JoinColumn(name = "metric_id"))
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    private Map<String, String> additionalData = new HashMap<>();

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    public MetricEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public double getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public void setMemoryUsagePercent(double memoryUsagePercent) {
        this.memoryUsagePercent = memoryUsagePercent;
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, String> additionalData) {
        this.additionalData = additionalData;
    }

    public Instant getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(Instant collectedAt) {
        this.collectedAt = collectedAt;
    }
}
