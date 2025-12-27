package com.apm.platform.application.mapper;

import com.apm.platform.application.dto.response.MonitoredSystemResponse;
import com.apm.platform.domain.entity.MonitoredSystem;

public class MonitoredSystemMapper {

    public static MonitoredSystemResponse toResponse(MonitoredSystem system) {
        if (system == null) {
            return null;
        }

        return new MonitoredSystemResponse(
            system.getId(),
            system.getName(),
            system.getBaseUrl(),
            system.getType().name(),
            system.getEnvironment().name(),
            system.getCollectionIntervalSeconds(),
            system.isActive(),
            system.getCurrentStatus().name(),
            system.getLastCheckAt(),
            system.getCreatedAt(),
            system.getUpdatedAt()
        );
    }

    public static MonitoredSystem.SystemType parseSystemType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("System type cannot be null or blank");
        }
        try {
            return MonitoredSystem.SystemType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid system type: " + type + 
                ". Valid values are: " + String.join(", ", getValidSystemTypes()));
        }
    }

    public static MonitoredSystem.Environment parseEnvironment(String environment) {
        if (environment == null || environment.isBlank()) {
            throw new IllegalArgumentException("Environment cannot be null or blank");
        }
        try {
            return MonitoredSystem.Environment.valueOf(environment.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid environment: " + environment + 
                ". Valid values are: " + String.join(", ", getValidEnvironments()));
        }
    }

    private static String[] getValidSystemTypes() {
        MonitoredSystem.SystemType[] types = MonitoredSystem.SystemType.values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].name();
        }
        return names;
    }

    private static String[] getValidEnvironments() {
        MonitoredSystem.Environment[] environments = MonitoredSystem.Environment.values();
        String[] names = new String[environments.length];
        for (int i = 0; i < environments.length; i++) {
            names[i] = environments[i].name();
        }
        return names;
    }
}
