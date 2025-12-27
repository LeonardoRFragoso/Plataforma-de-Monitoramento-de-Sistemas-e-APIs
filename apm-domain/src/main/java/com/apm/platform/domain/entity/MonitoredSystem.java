package com.apm.platform.domain.entity;

import com.apm.platform.domain.exception.InvalidSystemStateException;
import com.apm.platform.domain.valueobject.SystemStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class MonitoredSystem {
    private final String id;
    private String name;
    private String baseUrl;
    private SystemType type;
    private Environment environment;
    private int collectionIntervalSeconds;
    private boolean active;
    private SystemStatus currentStatus;
    private Instant lastCheckAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private MonitoredSystem(String id, String name, String baseUrl, SystemType type, 
                           Environment environment, int collectionIntervalSeconds, 
                           boolean active, SystemStatus currentStatus, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.type = type;
        this.environment = environment;
        this.collectionIntervalSeconds = collectionIntervalSeconds;
        this.active = active;
        this.currentStatus = currentStatus;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public static MonitoredSystem create(String name, String baseUrl, SystemType type, 
                                        Environment environment, int collectionIntervalSeconds) {
        validateName(name);
        validateBaseUrl(baseUrl);
        validateCollectionInterval(collectionIntervalSeconds);
        
        if (type == null) {
            throw new IllegalArgumentException("System type cannot be null");
        }
        if (environment == null) {
            throw new IllegalArgumentException("Environment cannot be null");
        }

        String id = UUID.randomUUID().toString();
        Instant now = Instant.now();
        return new MonitoredSystem(id, name, baseUrl, type, environment, 
                                  collectionIntervalSeconds, true, SystemStatus.UP, now);
    }

    public static MonitoredSystem reconstitute(String id, String name, String baseUrl, SystemType type,
                                              Environment environment, int collectionIntervalSeconds,
                                              boolean active, SystemStatus currentStatus, 
                                              Instant lastCheckAt, Instant createdAt, Instant updatedAt) {
        MonitoredSystem system = new MonitoredSystem(id, name, baseUrl, type, environment,
                                                    collectionIntervalSeconds, active, currentStatus, createdAt);
        system.lastCheckAt = lastCheckAt;
        system.updatedAt = updatedAt;
        return system;
    }

    public void updateDetails(String name, String baseUrl, SystemType type, 
                            Environment environment, int collectionIntervalSeconds) {
        validateName(name);
        validateBaseUrl(baseUrl);
        validateCollectionInterval(collectionIntervalSeconds);
        
        if (type == null) {
            throw new IllegalArgumentException("System type cannot be null");
        }
        if (environment == null) {
            throw new IllegalArgumentException("Environment cannot be null");
        }

        this.name = name;
        this.baseUrl = baseUrl;
        this.type = type;
        this.environment = environment;
        this.collectionIntervalSeconds = collectionIntervalSeconds;
        this.updatedAt = Instant.now();
    }

    public void updateStatus(SystemStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.currentStatus = newStatus;
        this.lastCheckAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (active) {
            throw new InvalidSystemStateException("System is already active");
        }
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (!active) {
            throw new InvalidSystemStateException("System is already inactive");
        }
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public boolean isHealthy() {
        return currentStatus.isHealthy();
    }

    public boolean isOperational() {
        return active && currentStatus.isOperational();
    }

    public boolean shouldCollectMetrics() {
        return active;
    }

    public boolean hasRecentCheck(int thresholdSeconds) {
        if (lastCheckAt == null) {
            return false;
        }
        Instant threshold = Instant.now().minusSeconds(thresholdSeconds);
        return lastCheckAt.isAfter(threshold);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("System name cannot be null or blank");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("System name cannot exceed 100 characters");
        }
    }

    private static void validateBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Base URL cannot be null or blank");
        }
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Base URL must start with http:// or https://");
        }
    }

    private static void validateCollectionInterval(int intervalSeconds) {
        if (intervalSeconds < 10) {
            throw new IllegalArgumentException("Collection interval must be at least 10 seconds");
        }
        if (intervalSeconds > 86400) {
            throw new IllegalArgumentException("Collection interval cannot exceed 24 hours (86400 seconds)");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public SystemType getType() {
        return type;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getCollectionIntervalSeconds() {
        return collectionIntervalSeconds;
    }

    public boolean isActive() {
        return active;
    }

    public SystemStatus getCurrentStatus() {
        return currentStatus;
    }

    public Instant getLastCheckAt() {
        return lastCheckAt;
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
        MonitoredSystem that = (MonitoredSystem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MonitoredSystem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", environment=" + environment +
                ", active=" + active +
                ", currentStatus=" + currentStatus +
                '}';
    }

    public enum SystemType {
        API,
        SERVICE,
        JOB,
        MICROSERVICE,
        MONOLITH
    }

    public enum Environment {
        PRODUCTION,
        STAGING,
        DEVELOPMENT,
        TEST
    }
}
