package com.apm.platform.domain.port.incoming;

import com.apm.platform.domain.entity.MonitoredSystem;

public interface UpdateMonitoredSystem {
    MonitoredSystem execute(String systemId, String name, String baseUrl, 
                           MonitoredSystem.SystemType type, MonitoredSystem.Environment environment,
                           int collectionIntervalSeconds);
}
