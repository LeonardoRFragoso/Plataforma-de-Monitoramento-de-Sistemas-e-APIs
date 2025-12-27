package com.apm.platform.domain.port.outgoing;

import com.apm.platform.domain.entity.Metric;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MetricRepository {
    Metric save(Metric metric);
    Optional<Metric> findById(String metricId);
    List<Metric> findBySystemId(String systemId);
    List<Metric> findBySystemIdAndTimeRange(String systemId, Instant startTime, Instant endTime);
    List<Metric> findRecentBySystemId(String systemId, int limit);
    long countBySystemId(String systemId);
    void deleteOlderThan(Instant timestamp);
}
