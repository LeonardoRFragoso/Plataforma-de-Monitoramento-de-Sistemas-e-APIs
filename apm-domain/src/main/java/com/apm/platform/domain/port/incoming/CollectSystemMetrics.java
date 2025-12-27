package com.apm.platform.domain.port.incoming;

import com.apm.platform.domain.entity.Metric;

public interface CollectSystemMetrics {
    Metric execute(String systemId);
}
