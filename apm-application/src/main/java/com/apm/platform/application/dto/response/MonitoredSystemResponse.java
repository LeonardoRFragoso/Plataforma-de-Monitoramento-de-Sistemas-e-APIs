package com.apm.platform.application.dto.response;

import java.time.Instant;

public class MonitoredSystemResponse {
    private final String id;
    private final String name;
    private final String baseUrl;
    private final String type;
    private final String environment;
    private final int collectionIntervalSeconds;
    private final boolean active;
    private final String currentStatus;
    private final Instant lastCheckAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    public MonitoredSystemResponse(String id, String name, String baseUrl, String type,
                                  String environment, int collectionIntervalSeconds, boolean active,
                                  String currentStatus, Instant lastCheckAt, Instant createdAt, 
                                  Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.type = type;
        this.environment = environment;
        this.collectionIntervalSeconds = collectionIntervalSeconds;
        this.active = active;
        this.currentStatus = currentStatus;
        this.lastCheckAt = lastCheckAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getType() {
        return type;
    }

    public String getEnvironment() {
        return environment;
    }

    public int getCollectionIntervalSeconds() {
        return collectionIntervalSeconds;
    }

    public boolean isActive() {
        return active;
    }

    public String getCurrentStatus() {
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
}
