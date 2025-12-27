package com.apm.platform.domain.event;

import com.apm.platform.domain.valueobject.SystemStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class SystemHealthDegradedEvent implements DomainEvent {
    private final String eventId;
    private final String systemId;
    private final SystemStatus previousStatus;
    private final SystemStatus currentStatus;
    private final String reason;
    private final Instant occurredAt;

    private SystemHealthDegradedEvent(String eventId, String systemId, SystemStatus previousStatus,
                                     SystemStatus currentStatus, String reason, Instant occurredAt) {
        this.eventId = eventId;
        this.systemId = systemId;
        this.previousStatus = previousStatus;
        this.currentStatus = currentStatus;
        this.reason = reason;
        this.occurredAt = occurredAt;
    }

    public static SystemHealthDegradedEvent create(String systemId, SystemStatus previousStatus,
                                                   SystemStatus currentStatus, String reason) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }
        if (previousStatus == null) {
            throw new IllegalArgumentException("Previous status cannot be null");
        }
        if (currentStatus == null) {
            throw new IllegalArgumentException("Current status cannot be null");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or blank");
        }

        String eventId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new SystemHealthDegradedEvent(eventId, systemId, previousStatus, currentStatus, reason, now);
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
        return systemId;
    }

    public String getSystemId() {
        return systemId;
    }

    public SystemStatus getPreviousStatus() {
        return previousStatus;
    }

    public SystemStatus getCurrentStatus() {
        return currentStatus;
    }

    public String getReason() {
        return reason;
    }

    public boolean isSystemDown() {
        return currentStatus == SystemStatus.DOWN;
    }

    public boolean isSignificantChange() {
        return (previousStatus == SystemStatus.UP && currentStatus == SystemStatus.DOWN) ||
               (previousStatus == SystemStatus.DEGRADED && currentStatus == SystemStatus.DOWN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemHealthDegradedEvent that = (SystemHealthDegradedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "SystemHealthDegradedEvent{" +
                "eventId='" + eventId + '\'' +
                ", systemId='" + systemId + '\'' +
                ", previousStatus=" + previousStatus +
                ", currentStatus=" + currentStatus +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
