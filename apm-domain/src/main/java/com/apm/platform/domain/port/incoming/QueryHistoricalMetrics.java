package com.apm.platform.domain.port.incoming;

import com.apm.platform.domain.entity.Metric;

import java.time.Instant;
import java.util.List;

public interface QueryHistoricalMetrics {
    List<Metric> execute(String systemId, Instant startTime, Instant endTime);
}
