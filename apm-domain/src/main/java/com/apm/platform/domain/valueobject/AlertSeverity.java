package com.apm.platform.domain.valueobject;

public enum AlertSeverity {
    WARNING(1, "Warning - requires attention"),
    CRITICAL(2, "Critical - requires immediate action");

    private final int level;
    private final String description;

    AlertSeverity(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMoreSevereThan(AlertSeverity other) {
        return this.level > other.level;
    }

    public boolean isCritical() {
        return this == CRITICAL;
    }
}
