package com.apm.platform.application.dto.response;

import java.time.Instant;

public class UptimeReportResponse {
    private final String systemId;
    private final String systemName;
    private final Instant periodStart;
    private final Instant periodEnd;
    private final double uptimePercentage;
    private final long totalChecks;
    private final long successfulChecks;
    private final long failedChecks;
    private final String availabilityClassification;

    public UptimeReportResponse(String systemId, String systemName, Instant periodStart,
                               Instant periodEnd, double uptimePercentage, long totalChecks,
                               long successfulChecks, long failedChecks, 
                               String availabilityClassification) {
        this.systemId = systemId;
        this.systemName = systemName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.uptimePercentage = uptimePercentage;
        this.totalChecks = totalChecks;
        this.successfulChecks = successfulChecks;
        this.failedChecks = failedChecks;
        this.availabilityClassification = availabilityClassification;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public Instant getPeriodStart() {
        return periodStart;
    }

    public Instant getPeriodEnd() {
        return periodEnd;
    }

    public double getUptimePercentage() {
        return uptimePercentage;
    }

    public long getTotalChecks() {
        return totalChecks;
    }

    public long getSuccessfulChecks() {
        return successfulChecks;
    }

    public long getFailedChecks() {
        return failedChecks;
    }

    public String getAvailabilityClassification() {
        return availabilityClassification;
    }
}
