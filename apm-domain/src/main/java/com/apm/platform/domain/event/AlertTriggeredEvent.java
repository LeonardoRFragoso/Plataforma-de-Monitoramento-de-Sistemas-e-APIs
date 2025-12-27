package com.apm.platform.domain.event;

import com.apm.platform.domain.valueobject.AlertSeverity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class AlertTriggeredEvent implements DomainEvent {
    private final String eventId;
    private final String alertId;
    private final String systemId;
    private final String ruleId;
    private final AlertSeverity severity;
    private final String message;
    private final Instant occurredAt;

    private AlertTriggeredEvent(String eventId, String alertId, String systemId, String ruleId,
                               AlertSeverity severity, String message, Instant occurredAt) {
        this.eventId = eventId;
        this.alertId = alertId;
        this.systemId = systemId;
        this.ruleId = ruleId;
        this.severity = severity;
        this.message = message;
        this.occurredAt = occurredAt;
    }

    public static AlertTriggeredEvent create(String alertId, String systemId, String ruleId,
                                            AlertSeverity severity, String message) {
        if (alertId == null || alertId.isBlank()) {
            throw new IllegalArgumentException("Alert ID cannot be null or blank");
        }
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }
        if (severity == null) {
            throw new IllegalArgumentException("Severity cannot be null");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank");
        }

        String eventId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new AlertTriggeredEvent(eventId, alertId, systemId, ruleId, severity, message, now);
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return alertId;
    }

    public String getAlertId() {
        return alertId;
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

    public boolean isCritical() {
        return severity.isCritical();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertTriggeredEvent that = (AlertTriggeredEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "AlertTriggeredEvent{" +
                "eventId='" + eventId + '\'' +
                ", alertId='" + alertId + '\'' +
                ", systemId='" + systemId + '\'' +
                ", severity=" + severity +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
