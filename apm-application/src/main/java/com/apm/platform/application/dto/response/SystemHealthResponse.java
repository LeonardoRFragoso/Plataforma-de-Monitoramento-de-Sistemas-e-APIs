package com.apm.platform.application.dto.response;

public class SystemHealthResponse {
    private final String systemId;
    private final String systemName;
    private final String status;
    private final String healthSummary;
    private final boolean isHealthy;
    private final boolean isOperational;

    public SystemHealthResponse(String systemId, String systemName, String status,
                               String healthSummary, boolean isHealthy, boolean isOperational) {
        this.systemId = systemId;
        this.systemName = systemName;
        this.status = status;
        this.healthSummary = healthSummary;
        this.isHealthy = isHealthy;
        this.isOperational = isOperational;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getStatus() {
        return status;
    }

    public String getHealthSummary() {
        return healthSummary;
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public boolean isOperational() {
        return isOperational;
    }
}
