package com.apm.platform.domain.entity;

import com.apm.platform.domain.exception.InvalidSystemStateException;
import com.apm.platform.domain.valueobject.AlertSeverity;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Alert {
    private final String id;
    private final String systemId;
    private final String ruleId;
    private final AlertSeverity severity;
    private final String message;
    private final Instant triggeredAt;
    private boolean resolved;
    private Instant resolvedAt;
    private String resolutionNotes;

    private Alert(String id, String systemId, String ruleId, AlertSeverity severity,
                 String message, Instant triggeredAt, boolean resolved, 
                 Instant resolvedAt, String resolutionNotes) {
        this.id = id;
        this.systemId = systemId;
        this.ruleId = ruleId;
        this.severity = severity;
        this.message = message;
        this.triggeredAt = triggeredAt;
        this.resolved = resolved;
        this.resolvedAt = resolvedAt;
        this.resolutionNotes = resolutionNotes;
    }

    public static Alert trigger(String systemId, String ruleId, AlertSeverity severity, String message) {
        validateSystemId(systemId);
        validateSeverity(severity);
        validateMessage(message);

        String id = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new Alert(id, systemId, ruleId, severity, message, now, false, null, null);
    }

    public static Alert reconstitute(String id, String systemId, String ruleId, AlertSeverity severity,
                                    String message, Instant triggeredAt, boolean resolved,
                                    Instant resolvedAt, String resolutionNotes) {
        return new Alert(id, systemId, ruleId, severity, message, triggeredAt, 
                        resolved, resolvedAt, resolutionNotes);
    }

    public void resolve(String notes) {
        if (resolved) {
            throw new InvalidSystemStateException("Alert is already resolved");
        }
        if (notes != null && notes.length() > 500) {
            throw new IllegalArgumentException("Resolution notes cannot exceed 500 characters");
        }

        this.resolved = true;
        this.resolvedAt = Instant.now();
        this.resolutionNotes = notes;
    }

    public void autoResolve() {
        resolve("Auto-resolved: system returned to normal state");
    }

    public Duration getDuration() {
        Instant endTime = resolved ? resolvedAt : Instant.now();
        return Duration.between(triggeredAt, endTime);
    }

    public boolean isActive() {
        return !resolved;
    }

    public boolean isCritical() {
        return severity.isCritical();
    }

    public boolean isLongRunning(Duration threshold) {
        return getDuration().compareTo(threshold) > 0;
    }

    private static void validateSystemId(String systemId) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }
    }

    private static void validateSeverity(AlertSeverity severity) {
        if (severity == null) {
            throw new IllegalArgumentException("Alert severity cannot be null");
        }
    }

    private static void validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Alert message cannot be null or blank");
        }
        if (message.length() > 500) {
            throw new IllegalArgumentException("Alert message cannot exceed 500 characters");
        }
    }

    public String getId() {
        return id;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert alert = (Alert) o;
        return Objects.equals(id, alert.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id='" + id + '\'' +
                ", systemId='" + systemId + '\'' +
                ", severity=" + severity +
                ", resolved=" + resolved +
                ", triggeredAt=" + triggeredAt +
                '}';
    }
}
