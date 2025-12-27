package com.apm.platform.domain.event;

import com.apm.platform.domain.valueobject.SystemStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class IncidentCreatedEvent implements DomainEvent {
    private final String eventId;
    private final String incidentId;
    private final String systemId;
    private final SystemStatus detectedStatus;
    private final String description;
    private final Instant occurredAt;

    private IncidentCreatedEvent(String eventId, String incidentId, String systemId,
                                SystemStatus detectedStatus, String description, Instant occurredAt) {
        this.eventId = eventId;
        this.incidentId = incidentId;
        this.systemId = systemId;
        this.detectedStatus = detectedStatus;
        this.description = description;
        this.occurredAt = occurredAt;
    }

    public static IncidentCreatedEvent create(String incidentId, String systemId,
                                             SystemStatus detectedStatus, String description) {
        if (incidentId == null || incidentId.isBlank()) {
            throw new IllegalArgumentException("Incident ID cannot be null or blank");
        }
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }
        if (detectedStatus == null) {
            throw new IllegalArgumentException("Detected status cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }

        String eventId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new IncidentCreatedEvent(eventId, incidentId, systemId, detectedStatus, description, now);
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
        return incidentId;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getSystemId() {
        return systemId;
    }

    public SystemStatus getDetectedStatus() {
        return detectedStatus;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCritical() {
        return detectedStatus == SystemStatus.DOWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncidentCreatedEvent that = (IncidentCreatedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "IncidentCreatedEvent{" +
                "eventId='" + eventId + '\'' +
                ", incidentId='" + incidentId + '\'' +
                ", systemId='" + systemId + '\'' +
                ", detectedStatus=" + detectedStatus +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
