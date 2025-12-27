package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.response.UptimeReportResponse;
import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.service.UptimeCalculationDomainService;
import com.apm.platform.domain.valueobject.UptimePercentage;

import java.time.Instant;
import java.util.List;

public class GenerateUptimeReportUseCase {

    private final MonitoredSystemRepository systemRepository;
    private final MetricRepository metricRepository;
    private final UptimeCalculationDomainService uptimeService;

    public GenerateUptimeReportUseCase(MonitoredSystemRepository systemRepository,
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

    public UptimeReportResponse execute(String systemId, Instant startTime, Instant endTime) {
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

        MonitoredSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        List<Metric> metrics = metricRepository.findBySystemIdAndTimeRange(systemId, startTime, endTime);

        UptimePercentage uptime = uptimeService.calculateUptimeFromMetrics(metrics);

        long totalChecks = metrics.size();
        long successfulChecks = metrics.stream().filter(Metric::isSuccessful).count();
        long failedChecks = totalChecks - successfulChecks;

        String classification = uptimeService.classifyAvailability(uptime);

        return new UptimeReportResponse(
            system.getId(),
            system.getName(),
            startTime,
            endTime,
            uptime.getValue(),
            totalChecks,
            successfulChecks,
            failedChecks,
            classification
        );
    }
}
