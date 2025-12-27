package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.response.SystemHealthResponse;
import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.event.SystemHealthDegradedEvent;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.incoming.EvaluateSystemHealth;
import com.apm.platform.domain.port.outgoing.DomainEventPublisher;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.service.HealthEvaluationDomainService;
import com.apm.platform.domain.valueobject.LatencyThreshold;
import com.apm.platform.domain.valueobject.SystemStatus;

import java.util.List;

public class EvaluateSystemHealthUseCase implements EvaluateSystemHealth {

    private static final int RECENT_METRICS_LIMIT = 10;
    private final MonitoredSystemRepository systemRepository;
    private final MetricRepository metricRepository;
    private final HealthEvaluationDomainService healthService;
    private final DomainEventPublisher eventPublisher;

    public EvaluateSystemHealthUseCase(MonitoredSystemRepository systemRepository,
                                      MetricRepository metricRepository,
                                      HealthEvaluationDomainService healthService,
                                      DomainEventPublisher eventPublisher) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        if (metricRepository == null) {
            throw new IllegalArgumentException("MetricRepository cannot be null");
        }
        if (healthService == null) {
            throw new IllegalArgumentException("HealthEvaluationDomainService cannot be null");
        }
        if (eventPublisher == null) {
            throw new IllegalArgumentException("DomainEventPublisher cannot be null");
        }

        this.systemRepository = systemRepository;
        this.metricRepository = metricRepository;
        this.healthService = healthService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SystemStatus execute(String systemId) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }

        MonitoredSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        List<Metric> recentMetrics = metricRepository.findRecentBySystemId(systemId, RECENT_METRICS_LIMIT);
        
        LatencyThreshold threshold = LatencyThreshold.defaultThresholds();
        SystemStatus newStatus = healthService.evaluateHealthFromMetrics(recentMetrics, threshold);

        SystemStatus previousStatus = system.getCurrentStatus();

        if (healthService.hasStatusChanged(previousStatus, newStatus)) {
            system.updateStatus(newStatus);
            systemRepository.save(system);

            if (healthService.isSignificantDegradation(previousStatus, newStatus)) {
                String reason = healthService.generateHealthSummary(newStatus, recentMetrics);
                SystemHealthDegradedEvent event = SystemHealthDegradedEvent.create(
                    systemId, previousStatus, newStatus, reason
                );
                eventPublisher.publish(event);
            }
        }

        return newStatus;
    }

    public SystemHealthResponse executeAndReturnResponse(String systemId) {
        MonitoredSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        SystemStatus status = execute(systemId);

        List<Metric> recentMetrics = metricRepository.findRecentBySystemId(systemId, RECENT_METRICS_LIMIT);
        String healthSummary = healthService.generateHealthSummary(status, recentMetrics);

        return new SystemHealthResponse(
            system.getId(),
            system.getName(),
            status.name(),
            healthSummary,
            status.isHealthy(),
            system.isOperational()
        );
    }
}
