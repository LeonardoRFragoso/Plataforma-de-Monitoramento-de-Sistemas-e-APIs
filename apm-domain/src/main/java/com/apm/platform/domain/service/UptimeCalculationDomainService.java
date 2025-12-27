package com.apm.platform.domain.service;

import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.valueobject.SystemStatus;
import com.apm.platform.domain.valueobject.UptimePercentage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class UptimeCalculationDomainService {

    public UptimePercentage calculateUptimeFromMetrics(List<Metric> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return UptimePercentage.zero();
        }

        long totalChecks = metrics.size();
        long successfulChecks = metrics.stream()
                .filter(Metric::isSuccessful)
                .count();

        return UptimePercentage.fromChecks(successfulChecks, totalChecks);
    }

    public UptimePercentage calculateUptimeFromTimeRange(Instant startTime, Instant endTime, 
                                                         Duration totalDowntime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time cannot be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (totalDowntime == null) {
            totalDowntime = Duration.ZERO;
        }

        Duration totalDuration = Duration.between(startTime, endTime);
        if (totalDuration.isZero()) {
            return UptimePercentage.perfect();
        }

        Duration uptime = totalDuration.minus(totalDowntime);
        if (uptime.isNegative()) {
            uptime = Duration.ZERO;
        }

        double uptimeSeconds = uptime.getSeconds();
        double totalSeconds = totalDuration.getSeconds();
        double percentage = (uptimeSeconds / totalSeconds) * 100.0;

        return UptimePercentage.of(percentage);
    }

    public double calculateAvailabilityRate(long successfulChecks, long totalChecks) {
        if (totalChecks == 0) {
            return 0.0;
        }
        return (successfulChecks * 100.0) / totalChecks;
    }

    public Duration estimateDowntime(List<Metric> metrics, int collectionIntervalSeconds) {
        if (metrics == null || metrics.isEmpty()) {
            return Duration.ZERO;
        }

        long failedChecks = metrics.stream()
                .filter(m -> !m.isSuccessful())
                .count();

        return Duration.ofSeconds(failedChecks * collectionIntervalSeconds);
    }

    public boolean meetsServiceLevelObjective(UptimePercentage uptime, double sloThreshold) {
        if (sloThreshold < 0 || sloThreshold > 100) {
            throw new IllegalArgumentException("SLO threshold must be between 0 and 100");
        }
        return uptime.getValue() >= sloThreshold;
    }

    public String classifyAvailability(UptimePercentage uptime) {
        double value = uptime.getValue();
        
        if (value >= 99.99) {
            return "Four Nines (99.99%)";
        } else if (value >= 99.95) {
            return "High Availability";
        } else if (value >= 99.9) {
            return "Three Nines (99.9%)";
        } else if (value >= 99.0) {
            return "Two Nines (99%)";
        } else if (value >= 95.0) {
            return "Acceptable";
        } else {
            return "Poor";
        }
    }

    public Duration calculateAllowedDowntime(Duration period, double uptimeTargetPercent) {
        if (period == null || period.isZero()) {
            throw new IllegalArgumentException("Period cannot be null or zero");
        }
        if (uptimeTargetPercent < 0 || uptimeTargetPercent > 100) {
            throw new IllegalArgumentException("Uptime target must be between 0 and 100");
        }

        long totalSeconds = period.getSeconds();
        double allowedDowntimePercent = 100.0 - uptimeTargetPercent;
        long allowedDowntimeSeconds = (long) (totalSeconds * (allowedDowntimePercent / 100.0));

        return Duration.ofSeconds(allowedDowntimeSeconds);
    }
}
