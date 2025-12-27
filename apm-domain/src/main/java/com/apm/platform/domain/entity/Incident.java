package com.apm.platform.domain.entity;

import com.apm.platform.domain.exception.InvalidSystemStateException;
import com.apm.platform.domain.valueobject.SystemStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Incident {
    private final String id;
    private final String systemId;
    private final SystemStatus detectedStatus;
    private final String description;
    private final Instant startedAt;
    private boolean resolved;
    private Instant resolvedAt;
    private Duration downtime;
    private String rootCause;

    private Incident(String id, String systemId, SystemStatus detectedStatus, String description,
                    Instant startedAt, boolean resolved, Instant resolvedAt, 
                    Duration downtime, String rootCause) {
        this.id = id;
        this.systemId = systemId;
        this.detectedStatus = detectedStatus;
        this.description = description;
        this.startedAt = startedAt;
        this.resolved = resolved;
        this.resolvedAt = resolvedAt;
        this.downtime = downtime;
        this.rootCause = rootCause;
    }

    public static Incident create(String systemId, SystemStatus detectedStatus, String description) {
        validateSystemId(systemId);
        validateDetectedStatus(detectedStatus);
        validateDescription(description);

        String id = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new Incident(id, systemId, detectedStatus, description, now, 
                           false, null, null, null);
    }

    public static Incident reconstitute(String id, String systemId, SystemStatus detectedStatus,
                                       String description, Instant startedAt, boolean resolved,
                                       Instant resolvedAt, Duration downtime, String rootCause) {
        return new Incident(id, systemId, detectedStatus, description, startedAt,
                           resolved, resolvedAt, downtime, rootCause);
    }

    public void resolve(String rootCause) {
        if (resolved) {
            throw new InvalidSystemStateException("Incident is already resolved");
        }
        if (rootCause == null || rootCause.isBlank()) {
            throw new IllegalArgumentException("Root cause cannot be null or blank");
        }
        if (rootCause.length() > 1000) {
            throw new IllegalArgumentException("Root cause description cannot exceed 1000 characters");
        }

        this.resolved = true;
        this.resolvedAt = Instant.now();
        this.downtime = Duration.between(startedAt, resolvedAt);
        this.rootCause = rootCause;
    }

    public void autoResolve() {
        resolve("Auto-resolved: system health check returned to normal");
    }

    public Duration getCurrentDuration() {
        Instant endTime = resolved ? resolvedAt : Instant.now();
        return Duration.between(startedAt, endTime);
    }

    public boolean isActive() {
        return !resolved;
    }

    public boolean isCritical() {
        return detectedStatus == SystemStatus.DOWN;
    }

    public boolean isLongRunning(Duration threshold) {
        return getCurrentDuration().compareTo(threshold) > 0;
    }

    private static void validateSystemId(String systemId) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }
    }

    private static void validateDetectedStatus(SystemStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Detected status cannot be null");
        }
        if (status == SystemStatus.UP) {
            throw new IllegalArgumentException("Cannot create incident for UP status");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Incident description cannot be null or blank");
        }
        if (description.length() > 500) {
            throw new IllegalArgumentException("Incident description cannot exceed 500 characters");
        }
    }

    public String getId() {
        return id;
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

    public Instant getStartedAt() {
        return startedAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public Duration getDowntime() {
        return downtime;
    }

    public String getRootCause() {
        return rootCause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Incident incident = (Incident) o;
        return Objects.equals(id, incident.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id='" + id + '\'' +
                ", systemId='" + systemId + '\'' +
                ", detectedStatus=" + detectedStatus +
                ", resolved=" + resolved +
                ", startedAt=" + startedAt +
                '}';
    }
}
