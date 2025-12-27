package com.apm.platform.domain.valueobject;

import java.util.Objects;

public final class UptimePercentage {
    private final double value;

    private UptimePercentage(double value) {
        this.value = value;
    }

    public static UptimePercentage of(double value) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException("Uptime percentage must be between 0 and 100");
        }
        return new UptimePercentage(value);
    }

    public static UptimePercentage fromChecks(long successfulChecks, long totalChecks) {
        if (totalChecks <= 0) {
            throw new IllegalArgumentException("Total checks must be greater than zero");
        }
        if (successfulChecks < 0 || successfulChecks > totalChecks) {
            throw new IllegalArgumentException("Successful checks must be between 0 and total checks");
        }
        double percentage = (successfulChecks * 100.0) / totalChecks;
        return new UptimePercentage(percentage);
    }

    public static UptimePercentage zero() {
        return new UptimePercentage(0.0);
    }

    public static UptimePercentage perfect() {
        return new UptimePercentage(100.0);
    }

    public double getValue() {
        return value;
    }

    public boolean isAbove(double threshold) {
        return value > threshold;
    }

    public boolean isBelow(double threshold) {
        return value < threshold;
    }

    public boolean isPerfect() {
        return Double.compare(value, 100.0) == 0;
    }

    public boolean isCritical() {
        return value < 95.0;
    }

    public boolean isExcellent() {
        return value >= 99.9;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UptimePercentage that = (UptimePercentage) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("%.2f%%", value);
    }
}
