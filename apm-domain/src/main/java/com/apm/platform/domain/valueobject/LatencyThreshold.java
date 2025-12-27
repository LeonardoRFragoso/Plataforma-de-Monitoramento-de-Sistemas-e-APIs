package com.apm.platform.domain.valueobject;

import java.util.Objects;

public final class LatencyThreshold {
    private final long warningMs;
    private final long criticalMs;

    private LatencyThreshold(long warningMs, long criticalMs) {
        this.warningMs = warningMs;
        this.criticalMs = criticalMs;
    }

    public static LatencyThreshold create(long warningMs, long criticalMs) {
        if (warningMs <= 0) {
            throw new IllegalArgumentException("Warning threshold must be greater than zero");
        }
        if (criticalMs <= 0) {
            throw new IllegalArgumentException("Critical threshold must be greater than zero");
        }
        if (criticalMs <= warningMs) {
            throw new IllegalArgumentException("Critical threshold must be greater than warning threshold");
        }
        return new LatencyThreshold(warningMs, criticalMs);
    }

    public static LatencyThreshold defaultThresholds() {
        return new LatencyThreshold(1000, 3000);
    }

    public long getWarningMs() {
        return warningMs;
    }

    public long getCriticalMs() {
        return criticalMs;
    }

    public boolean isWarning(long latencyMs) {
        return latencyMs > warningMs && latencyMs <= criticalMs;
    }

    public boolean isCritical(long latencyMs) {
        return latencyMs > criticalMs;
    }

    public boolean isNormal(long latencyMs) {
        return latencyMs <= warningMs;
    }

    public AlertSeverity evaluateSeverity(long latencyMs) {
        if (isCritical(latencyMs)) {
            return AlertSeverity.CRITICAL;
        }
        if (isWarning(latencyMs)) {
            return AlertSeverity.WARNING;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatencyThreshold that = (LatencyThreshold) o;
        return warningMs == that.warningMs && criticalMs == that.criticalMs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(warningMs, criticalMs);
    }

    @Override
    public String toString() {
        return "LatencyThreshold{" +
                "warningMs=" + warningMs +
                ", criticalMs=" + criticalMs +
                '}';
    }
}
