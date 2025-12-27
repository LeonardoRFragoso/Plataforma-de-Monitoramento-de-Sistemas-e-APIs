package com.apm.platform.application.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class QueryMetricsRequest {
    private final String systemId;
    private final Instant startTime;
    private final Instant endTime;

    @JsonCreator
    public QueryMetricsRequest(
            @JsonProperty("systemId") String systemId,
            @JsonProperty("startTime") Instant startTime,
            @JsonProperty("endTime") Instant endTime) {
        this.systemId = systemId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (systemId == null || systemId.isBlank()) {
            errors.add("System ID is required");
        }

        if (startTime == null) {
            errors.add("Start time is required");
        }

        if (endTime == null) {
            errors.add("End time is required");
        }

        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            errors.add("End time must be after start time");
        }

        return errors;
    }

    public boolean isValid() {
        return validate().isEmpty();
    }

    public String getSystemId() {
        return systemId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }
}
