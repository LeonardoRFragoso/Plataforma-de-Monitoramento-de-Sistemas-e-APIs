package com.apm.platform.domain.service;

import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.valueobject.HealthCheckResult;
import com.apm.platform.domain.valueobject.LatencyThreshold;
import com.apm.platform.domain.valueobject.SystemStatus;

import java.util.List;

public class HealthEvaluationDomainService {

    private static final double HIGH_CPU_THRESHOLD = 80.0;
    private static final double HIGH_MEMORY_THRESHOLD = 85.0;
    private static final int CONSECUTIVE_ERRORS_FOR_DEGRADED = 2;
    private static final int CONSECUTIVE_ERRORS_FOR_DOWN = 3;

    public SystemStatus evaluateHealthFromMetrics(List<Metric> recentMetrics, LatencyThreshold latencyThreshold) {
        if (recentMetrics == null || recentMetrics.isEmpty()) {
            return SystemStatus.UP;
        }

        int consecutiveErrors = countConsecutiveErrors(recentMetrics);
        if (consecutiveErrors >= CONSECUTIVE_ERRORS_FOR_DOWN) {
            return SystemStatus.DOWN;
        }

        boolean hasCriticalLatency = recentMetrics.stream()
                .anyMatch(m -> m.getLatencyMs() > latencyThreshold.getCriticalMs());
        
        boolean hasHighResourceUsage = recentMetrics.stream()
                .anyMatch(m -> m.getCpuUsagePercent() > HIGH_CPU_THRESHOLD || 
                              m.getMemoryUsagePercent() > HIGH_MEMORY_THRESHOLD);

        if (consecutiveErrors >= CONSECUTIVE_ERRORS_FOR_DEGRADED || hasCriticalLatency || hasHighResourceUsage) {
            return SystemStatus.DEGRADED;
        }

        return SystemStatus.UP;
    }

    public SystemStatus evaluateHealthFromCheckResult(HealthCheckResult result) {
        if (result == null) {
            return SystemStatus.DOWN;
        }
        return result.getStatus();
    }

    public boolean hasStatusChanged(SystemStatus previousStatus, SystemStatus currentStatus) {
        if (previousStatus == null || currentStatus == null) {
            return false;
        }
        return previousStatus != currentStatus;
    }

    public boolean isSignificantDegradation(SystemStatus previousStatus, SystemStatus currentStatus) {
        if (previousStatus == null || currentStatus == null) {
            return false;
        }

        return (previousStatus == SystemStatus.UP && currentStatus == SystemStatus.DEGRADED) ||
               (previousStatus == SystemStatus.UP && currentStatus == SystemStatus.DOWN) ||
               (previousStatus == SystemStatus.DEGRADED && currentStatus == SystemStatus.DOWN);
    }

    public boolean isRecovery(SystemStatus previousStatus, SystemStatus currentStatus) {
        if (previousStatus == null || currentStatus == null) {
            return false;
        }

        return (previousStatus == SystemStatus.DOWN && currentStatus == SystemStatus.DEGRADED) ||
               (previousStatus == SystemStatus.DOWN && currentStatus == SystemStatus.UP) ||
               (previousStatus == SystemStatus.DEGRADED && currentStatus == SystemStatus.UP);
    }

    private int countConsecutiveErrors(List<Metric> metrics) {
        int count = 0;
        for (int i = metrics.size() - 1; i >= 0; i--) {
            if (metrics.get(i).hasError() || !metrics.get(i).isSuccessful()) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public String generateHealthSummary(SystemStatus status, List<Metric> recentMetrics) {
        if (recentMetrics == null || recentMetrics.isEmpty()) {
            return "No recent metrics available";
        }

        double avgLatency = recentMetrics.stream()
                .mapToLong(Metric::getLatencyMs)
                .average()
                .orElse(0);

        long errorCount = recentMetrics.stream()
                .filter(Metric::hasError)
                .count();

        return String.format("Status: %s, Avg Latency: %.0fms, Errors: %d/%d",
                status, avgLatency, errorCount, recentMetrics.size());
    }
}
