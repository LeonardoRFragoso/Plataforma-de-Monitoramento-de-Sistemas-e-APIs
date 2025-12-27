package com.apm.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "incidents", indexes = {
    @Index(name = "idx_incident_system_id", columnList = "system_id"),
    @Index(name = "idx_incident_resolved", columnList = "resolved"),
    @Index(name = "idx_incident_started_at", columnList = "started_at")
})
public class IncidentEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "system_id", nullable = false, length = 36)
    private String systemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "detected_status", nullable = false, length = 20)
    private SystemStatus detectedStatus;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "resolved", nullable = false)
    private boolean resolved;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "downtime_seconds")
    private Long downtimeSeconds;

    @Column(name = "root_cause", length = 1000)
    private String rootCause;

    public IncidentEntity() {
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

    public SystemStatus getDetectedStatus() {
        return detectedStatus;
    }

    public void setDetectedStatus(SystemStatus detectedStatus) {
        this.detectedStatus = detectedStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Long getDowntimeSeconds() {
        return downtimeSeconds;
    }

    public void setDowntimeSeconds(Long downtimeSeconds) {
        this.downtimeSeconds = downtimeSeconds;
    }

    public Duration getDowntime() {
        return downtimeSeconds != null ? Duration.ofSeconds(downtimeSeconds) : null;
    }

    public void setDowntime(Duration downtime) {
        this.downtimeSeconds = downtime != null ? downtime.getSeconds() : null;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public enum SystemStatus {
        UP, DEGRADED, DOWN
    }
}
