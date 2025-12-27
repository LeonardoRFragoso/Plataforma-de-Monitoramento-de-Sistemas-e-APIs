package com.apm.platform.application.usecase;

import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.incoming.CalculateSystemUptime;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.service.UptimeCalculationDomainService;
import com.apm.platform.domain.valueobject.UptimePercentage;

import java.time.Instant;
import java.util.List;

public class CalculateSystemUptimeUseCase implements CalculateSystemUptime {

    private final MonitoredSystemRepository systemRepository;
    private final MetricRepository metricRepository;
    private final UptimeCalculationDomainService uptimeService;

    public CalculateSystemUptimeUseCase(MonitoredSystemRepository systemRepository,
                                       MetricRepository metricRepository,
                                       UptimeCalculationDomainService uptimeService) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        if (metricRepository == null) {
            throw new IllegalArgumentException("MetricRepository cannot be null");
        }
        if (uptimeService == null) {
            throw new IllegalArgumentException("UptimeCalculationDomainService cannot be null");
        }

        this.systemRepository = systemRepository;
        this.metricRepository = metricRepository;
        this.uptimeService = uptimeService;
    }

    @Override
    public UptimePercentage execute(String systemId, Instant startTime, Instant endTime) {
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

        List<Metric> metrics = metricRepository.findBySystemIdAndTimeRange(systemId, startTime, endTime);

        return uptimeService.calculateUptimeFromMetrics(metrics);
    }
}
