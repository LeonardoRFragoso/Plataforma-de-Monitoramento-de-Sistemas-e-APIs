package com.apm.platform.domain.port.outgoing;

import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.valueobject.MetricSnapshot;

public interface MetricCollectorGateway {
    MetricSnapshot collectMetrics(MonitoredSystem system);
    boolean isReachable(MonitoredSystem system);
}
