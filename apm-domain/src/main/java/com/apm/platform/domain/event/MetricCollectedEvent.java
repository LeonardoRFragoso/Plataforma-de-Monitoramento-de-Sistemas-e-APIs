package com.apm.platform.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class MetricCollectedEvent implements DomainEvent {
    private final String eventId;
    private final String metricId;
    private final String systemId;
    private final long latencyMs;
    private final int statusCode;
    private final boolean hasError;
    private final Instant occurredAt;

    private MetricCollectedEvent(String eventId, String metricId, String systemId,
                                long latencyMs, int statusCode, boolean hasError, Instant occurredAt) {
        this.eventId = eventId;
        this.metricId = metricId;
        this.systemId = systemId;
        this.latencyMs = latencyMs;
        this.statusCode = statusCode;
        this.hasError = hasError;
        this.occurredAt = occurredAt;
    }

    public static MetricCollectedEvent create(String metricId, String systemId, long latencyMs,
                                             int statusCode, boolean hasError) {
        if (metricId == null || metricId.isBlank()) {
            throw new IllegalArgumentException("Metric ID cannot be null or blank");
        }
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }

        String eventId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new MetricCollectedEvent(eventId, metricId, systemId, latencyMs, statusCode, hasError, now);
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
        return metricId;
    }

    public String getMetricId() {
        return metricId;
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

    public boolean hasError() {
        return hasError;
    }

    public boolean isSuccessful() {
        return !hasError && statusCode >= 200 && statusCode < 300;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricCollectedEvent that = (MetricCollectedEvent) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "MetricCollectedEvent{" +
                "eventId='" + eventId + '\'' +
                ", metricId='" + metricId + '\'' +
                ", systemId='" + systemId + '\'' +
                ", latencyMs=" + latencyMs +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
