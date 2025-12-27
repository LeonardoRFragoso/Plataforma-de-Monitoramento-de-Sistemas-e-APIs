package com.apm.platform.domain.valueobject;

public enum SystemStatus {
    UP("System is healthy and responding normally"),
    DEGRADED("System is experiencing performance issues"),
    DOWN("System is not responding or unreachable");

    private final String description;

    SystemStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHealthy() {
        return this == UP;
    }

    public boolean isDegraded() {
        return this == DEGRADED;
    }

    public boolean isDown() {
        return this == DOWN;
    }

    public boolean isOperational() {
        return this == UP || this == DEGRADED;
    }
}
