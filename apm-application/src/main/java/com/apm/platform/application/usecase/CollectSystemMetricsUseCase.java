package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.response.MetricResponse;
import com.apm.platform.application.mapper.MetricMapper;
import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.event.MetricCollectedEvent;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.incoming.CollectSystemMetrics;
import com.apm.platform.domain.port.outgoing.DomainEventPublisher;
import com.apm.platform.domain.port.outgoing.MetricCollectorGateway;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.valueobject.MetricSnapshot;

public class CollectSystemMetricsUseCase implements CollectSystemMetrics {

    private final MonitoredSystemRepository systemRepository;
    private final MetricRepository metricRepository;
    private final MetricCollectorGateway collectorGateway;
    private final DomainEventPublisher eventPublisher;

    public CollectSystemMetricsUseCase(MonitoredSystemRepository systemRepository,
                                      MetricRepository metricRepository,
                                      MetricCollectorGateway collectorGateway,
                                      DomainEventPublisher eventPublisher) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        if (metricRepository == null) {
            throw new IllegalArgumentException("MetricRepository cannot be null");
        }
        if (collectorGateway == null) {
            throw new IllegalArgumentException("MetricCollectorGateway cannot be null");
        }
        if (eventPublisher == null) {
            throw new IllegalArgumentException("DomainEventPublisher cannot be null");
        }

        this.systemRepository = systemRepository;
        this.metricRepository = metricRepository;
        this.collectorGateway = collectorGateway;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Metric execute(String systemId) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }

        MonitoredSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        if (!system.shouldCollectMetrics()) {
            throw new IllegalStateException("System is not active for metric collection: " + systemId);
        }

        MetricSnapshot snapshot = collectorGateway.collectMetrics(system);
        
        Metric metric = Metric.fromSnapshot(systemId, snapshot);
        Metric savedMetric = metricRepository.save(metric);

        MetricCollectedEvent event = MetricCollectedEvent.create(
            savedMetric.getId(),
            savedMetric.getSystemId(),
            savedMetric.getLatencyMs(),
            savedMetric.getStatusCode(),
            savedMetric.hasError()
        );
        eventPublisher.publish(event);

        return savedMetric;
    }

    public MetricResponse executeAndReturnResponse(String systemId) {
        Metric metric = execute(systemId);
        return MetricMapper.toResponse(metric);
    }
}
