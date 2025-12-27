package com.apm.platform.application.dto.response;

import java.time.Instant;

public class AlertRuleResponse {
    private final String id;
    private final String systemId;
    private final String name;
    private final String type;
    private final String severity;
    private final double thresholdValue;
    private final int consecutiveViolations;
    private final boolean enabled;
    private final Instant createdAt;
    private final Instant updatedAt;

    public AlertRuleResponse(String id, String systemId, String name, String type,
                            String severity, double thresholdValue, int consecutiveViolations,
                            boolean enabled, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.systemId = systemId;
        this.name = name;
        this.type = type;
        this.severity = severity;
        this.thresholdValue = thresholdValue;
        this.consecutiveViolations = consecutiveViolations;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public double getThresholdValue() {
        return thresholdValue;
    }

    public int getConsecutiveViolations() {
        return consecutiveViolations;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
