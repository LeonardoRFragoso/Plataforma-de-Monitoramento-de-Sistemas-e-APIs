package com.apm.platform.application.mapper;

import com.apm.platform.application.dto.response.AlertResponse;
import com.apm.platform.domain.entity.Alert;
import com.apm.platform.domain.valueobject.AlertSeverity;

public class AlertMapper {

    public static AlertResponse toResponse(Alert alert) {
        if (alert == null) {
            return null;
        }

        return new AlertResponse(
            alert.getId(),
            alert.getSystemId(),
            alert.getRuleId(),
            alert.getSeverity().name(),
            alert.getMessage(),
            alert.getTriggeredAt(),
            alert.isResolved(),
            alert.getResolvedAt(),
            alert.getResolutionNotes()
        );
    }

    public static AlertSeverity parseSeverity(String severity) {
        if (severity == null || severity.isBlank()) {
            throw new IllegalArgumentException("Severity cannot be null or blank");
        }
        try {
            return AlertSeverity.valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid severity: " + severity + 
                ". Valid values are: WARNING, CRITICAL");
        }
    }
}
