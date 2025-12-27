package com.apm.platform.application.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class CreateAlertRuleRequest {
    private final String systemId;
    private final String name;
    private final String type;
    private final String severity;
    private final Double thresholdValue;
    private final Integer consecutiveViolations;

    @JsonCreator
    public CreateAlertRuleRequest(
            @JsonProperty("systemId") String systemId,
            @JsonProperty("name") String name,
            @JsonProperty("type") String type,
            @JsonProperty("severity") String severity,
            @JsonProperty("thresholdValue") Double thresholdValue,
            @JsonProperty("consecutiveViolations") Integer consecutiveViolations) {
        this.systemId = systemId;
        this.name = name;
        this.type = type;
        this.severity = severity;
        this.thresholdValue = thresholdValue;
        this.consecutiveViolations = consecutiveViolations;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (systemId == null || systemId.isBlank()) {
            errors.add("System ID is required");
        }

        if (name == null || name.isBlank()) {
            errors.add("Rule name is required");
        } else if (name.length() > 100) {
            errors.add("Rule name must not exceed 100 characters");
        }

        if (type == null || type.isBlank()) {
            errors.add("Rule type is required");
        }

        if (severity == null || severity.isBlank()) {
            errors.add("Severity is required");
        }

        if (thresholdValue == null) {
            errors.add("Threshold value is required");
        } else if (thresholdValue < 0) {
            errors.add("Threshold value cannot be negative");
        }

        if (consecutiveViolations == null) {
            errors.add("Consecutive violations is required");
        } else if (consecutiveViolations < 1) {
            errors.add("Consecutive violations must be at least 1");
        } else if (consecutiveViolations > 100) {
            errors.add("Consecutive violations cannot exceed 100");
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

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public Double getThresholdValue() {
        return thresholdValue;
    }

    public Integer getConsecutiveViolations() {
        return consecutiveViolations;
    }
}
