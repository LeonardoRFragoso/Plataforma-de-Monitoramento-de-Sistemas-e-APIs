package com.apm.platform.application.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UpdateMonitoredSystemRequest {
    private final String systemId;
    private final String name;
    private final String baseUrl;
    private final String type;
    private final String environment;
    private final Integer collectionIntervalSeconds;

    @JsonCreator
    public UpdateMonitoredSystemRequest(
            @JsonProperty("systemId") String systemId,
            @JsonProperty("name") String name,
            @JsonProperty("baseUrl") String baseUrl,
            @JsonProperty("type") String type,
            @JsonProperty("environment") String environment,
            @JsonProperty("collectionIntervalSeconds") Integer collectionIntervalSeconds) {
        this.systemId = systemId;
        this.name = name;
        this.baseUrl = baseUrl;
        this.type = type;
        this.environment = environment;
        this.collectionIntervalSeconds = collectionIntervalSeconds;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (systemId == null || systemId.isBlank()) {
            errors.add("System ID is required");
        }

        if (name == null || name.isBlank()) {
            errors.add("Name is required");
        } else if (name.length() > 100) {
            errors.add("Name must not exceed 100 characters");
        }

        if (baseUrl == null || baseUrl.isBlank()) {
            errors.add("Base URL is required");
        } else if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            errors.add("Base URL must start with http:// or https://");
        }

        if (type == null || type.isBlank()) {
            errors.add("Type is required");
        }

        if (environment == null || environment.isBlank()) {
            errors.add("Environment is required");
        }

        if (collectionIntervalSeconds == null) {
            errors.add("Collection interval is required");
        } else if (collectionIntervalSeconds < 10) {
            errors.add("Collection interval must be at least 10 seconds");
        } else if (collectionIntervalSeconds > 86400) {
            errors.add("Collection interval cannot exceed 24 hours");
        }

        return errors;
    }

    public boolean isValid() {
        return validate().isEmpty();
    }

    public String getSystemId() {
        return systemId;
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

    public Integer getCollectionIntervalSeconds() {
        return collectionIntervalSeconds;
    }
}
