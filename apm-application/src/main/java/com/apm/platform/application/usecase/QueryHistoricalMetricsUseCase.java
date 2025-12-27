package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.request.QueryMetricsRequest;
import com.apm.platform.application.dto.response.MetricResponse;
import com.apm.platform.application.mapper.MetricMapper;
import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.incoming.QueryHistoricalMetrics;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;

import java.time.Instant;
import java.util.List;

public class QueryHistoricalMetricsUseCase implements QueryHistoricalMetrics {

    private final MonitoredSystemRepository systemRepository;
    private final MetricRepository metricRepository;

    public QueryHistoricalMetricsUseCase(MonitoredSystemRepository systemRepository,
                                        MetricRepository metricRepository) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        if (metricRepository == null) {
            throw new IllegalArgumentException("MetricRepository cannot be null");
        }

        this.systemRepository = systemRepository;
        this.metricRepository = metricRepository;
    }

    @Override
    public List<Metric> execute(String systemId, Instant startTime, Instant endTime) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        return metricRepository.findBySystemIdAndTimeRange(systemId, startTime, endTime);
    }

    public List<MetricResponse> execute(QueryMetricsRequest request) {
        List<String> validationErrors = request.validate();
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", validationErrors));
        }

        List<Metric> metrics = execute(
            request.getSystemId(),
            request.getStartTime(),
            request.getEndTime()
        );

        return MetricMapper.toResponseList(metrics);
    }
}
