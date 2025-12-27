package com.apm.platform.domain.entity;

import com.apm.platform.domain.valueobject.AlertSeverity;
import com.apm.platform.domain.valueobject.LatencyThreshold;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class AlertRule {
    private final String id;
    private final String systemId;
    private String name;
    private AlertRuleType type;
    private AlertSeverity severity;
    private double thresholdValue;
    private int consecutiveViolations;
    private boolean enabled;
    private final Instant createdAt;
    private Instant updatedAt;

    private AlertRule(String id, String systemId, String name, AlertRuleType type, 
                     AlertSeverity severity, double thresholdValue, int consecutiveViolations,
                     boolean enabled, Instant createdAt) {
        this.id = id;
        this.systemId = systemId;
        this.name = name;
        this.type = type;
        this.severity = severity;
        this.thresholdValue = thresholdValue;
        this.consecutiveViolations = consecutiveViolations;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public static AlertRule create(String systemId, String name, AlertRuleType type,
                                  AlertSeverity severity, double thresholdValue, int consecutiveViolations) {
        validateSystemId(systemId);
        validateName(name);
        validateType(type);
        validateSeverity(severity);
        validateThresholdValue(type, thresholdValue);
        validateConsecutiveViolations(consecutiveViolations);

        String id = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new AlertRule(id, systemId, name, type, severity, thresholdValue, 
                            consecutiveViolations, true, now);
    }

    public static AlertRule reconstitute(String id, String systemId, String name, AlertRuleType type,
                                        AlertSeverity severity, double thresholdValue, 
                                        int consecutiveViolations, boolean enabled,
                                        Instant createdAt, Instant updatedAt) {
        AlertRule rule = new AlertRule(id, systemId, name, type, severity, thresholdValue,
                                      consecutiveViolations, enabled, createdAt);
        rule.updatedAt = updatedAt;
        return rule;
    }

    public boolean isViolated(Metric metric) {
        if (!enabled) {
            return false;
        }

        return switch (type) {
            case LATENCY_MS -> metric.getLatencyMs() > thresholdValue;
            case ERROR_RATE_PERCENT -> metric.hasError();
            case CPU_USAGE_PERCENT -> metric.getCpuUsagePercent() > thresholdValue;
            case MEMORY_USAGE_PERCENT -> metric.getMemoryUsagePercent() > thresholdValue;
            case STATUS_CODE -> metric.getStatusCode() >= (int) thresholdValue;
        };
    }

    public void updateRule(String name, AlertRuleType type, AlertSeverity severity, 
                          double thresholdValue, int consecutiveViolations) {
        validateName(name);
        validateType(type);
        validateSeverity(severity);
        validateThresholdValue(type, thresholdValue);
        validateConsecutiveViolations(consecutiveViolations);

        this.name = name;
        this.type = type;
        this.severity = severity;
        this.thresholdValue = thresholdValue;
        this.consecutiveViolations = consecutiveViolations;
        this.updatedAt = Instant.now();
    }

    public void enable() {
        this.enabled = true;
        this.updatedAt = Instant.now();
    }

    public void disable() {
        this.enabled = false;
        this.updatedAt = Instant.now();
    }

    private static void validateSystemId(String systemId) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Alert rule name cannot be null or blank");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Alert rule name cannot exceed 100 characters");
        }
    }

    private static void validateType(AlertRuleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Alert rule type cannot be null");
        }
    }

    private static void validateSeverity(AlertSeverity severity) {
        if (severity == null) {
            throw new IllegalArgumentException("Alert severity cannot be null");
        }
    }

    private static void validateThresholdValue(AlertRuleType type, double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Threshold value cannot be negative");
        }

        switch (type) {
            case CPU_USAGE_PERCENT, MEMORY_USAGE_PERCENT, ERROR_RATE_PERCENT -> {
                if (value > 100) {
                    throw new IllegalArgumentException("Percentage threshold cannot exceed 100");
                }
            }
        }
    }

    private static void validateConsecutiveViolations(int violations) {
        if (violations < 1) {
            throw new IllegalArgumentException("Consecutive violations must be at least 1");
        }
        if (violations > 100) {
            throw new IllegalArgumentException("Consecutive violations cannot exceed 100");
        }
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

    public AlertRuleType getType() {
        return type;
    }

    public AlertSeverity getSeverity() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertRule alertRule = (AlertRule) o;
        return Objects.equals(id, alertRule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AlertRule{" +
                "id='" + id + '\'' +
                ", systemId='" + systemId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", severity=" + severity +
                ", enabled=" + enabled +
                '}';
    }

    public enum AlertRuleType {
        LATENCY_MS,
        ERROR_RATE_PERCENT,
        CPU_USAGE_PERCENT,
        MEMORY_USAGE_PERCENT,
        STATUS_CODE
    }
}
