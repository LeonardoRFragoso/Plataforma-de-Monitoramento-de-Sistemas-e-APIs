package com.apm.platform.domain.port.incoming;

import com.apm.platform.domain.entity.MonitoredSystem;

public interface RegisterMonitoredSystem {
    MonitoredSystem execute(String name, String baseUrl, MonitoredSystem.SystemType type,
                           MonitoredSystem.Environment environment, int collectionIntervalSeconds);
}
