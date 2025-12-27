package com.apm.platform.application.dto.response;

import java.time.Instant;

public class AlertResponse {
    private final String id;
    private final String systemId;
    private final String ruleId;
    private final String severity;
    private final String message;
    private final Instant triggeredAt;
    private final boolean resolved;
    private final Instant resolvedAt;
    private final String resolutionNotes;

    public AlertResponse(String id, String systemId, String ruleId, String severity,
                        String message, Instant triggeredAt, boolean resolved,
                        Instant resolvedAt, String resolutionNotes) {
        this.id = id;
        this.systemId = systemId;
        this.ruleId = ruleId;
        this.severity = severity;
        this.message = message;
        this.triggeredAt = triggeredAt;
        this.resolved = resolved;
        this.resolvedAt = resolvedAt;
        this.resolutionNotes = resolutionNotes;
    }

    public String getId() {
        return id;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }
}
