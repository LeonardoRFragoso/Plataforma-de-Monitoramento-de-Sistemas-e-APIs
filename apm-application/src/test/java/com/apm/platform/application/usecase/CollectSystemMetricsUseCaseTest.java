package com.apm.platform.application.usecase;

import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.outgoing.DomainEventPublisher;
import com.apm.platform.domain.port.outgoing.MetricCollectorGateway;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.valueobject.MetricSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollectSystemMetricsUseCaseTest {

    private MonitoredSystemRepository systemRepository;
    private MetricRepository metricRepository;
    private MetricCollectorGateway collectorGateway;
    private DomainEventPublisher eventPublisher;
    private CollectSystemMetricsUseCase useCase;

    @BeforeEach
    void setUp() {
        systemRepository = mock(MonitoredSystemRepository.class);
        metricRepository = mock(MetricRepository.class);
        collectorGateway = mock(MetricCollectorGateway.class);
        eventPublisher = mock(DomainEventPublisher.class);
        
        useCase = new CollectSystemMetricsUseCase(
            systemRepository, metricRepository, collectorGateway, eventPublisher
        );
    }

    @Test
    void shouldCollectMetricsForActiveSystem() {
        String systemId = "system-123";
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.test.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        MetricSnapshot snapshot = MetricSnapshot.create(150, 45.5, 60.0, 200, false);

        when(systemRepository.findById(systemId)).thenReturn(Optional.of(system));
        when(collectorGateway.collectMetrics(system)).thenReturn(snapshot);
        when(metricRepository.save(any(Metric.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Metric result = useCase.execute(systemId);

        assertNotNull(result);
        assertEquals(systemId, result.getSystemId());
        assertEquals(150, result.getLatencyMs());
        assertEquals(200, result.getStatusCode());
        assertFalse(result.hasError());

        verify(systemRepository).findById(systemId);
        verify(collectorGateway).collectMetrics(system);
        verify(metricRepository).save(any(Metric.class));
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenSystemNotFound() {
        String systemId = "non-existent";

        when(systemRepository.findById(systemId)).thenReturn(Optional.empty());

        assertThrows(MonitoredSystemNotFoundException.class, () ->
            useCase.execute(systemId)
        );

        verify(systemRepository).findById(systemId);
        verify(collectorGateway, never()).collectMetrics(any());
        verify(metricRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSystemIsInactive() {
        String systemId = "system-123";
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.test.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );
        system.deactivate();

        when(systemRepository.findById(systemId)).thenReturn(Optional.of(system));

        assertThrows(IllegalStateException.class, () ->
            useCase.execute(systemId)
        );

        verify(collectorGateway, never()).collectMetrics(any());
    }

    @Test
    void shouldThrowExceptionWhenSystemIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            useCase.execute(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new CollectSystemMetricsUseCase(null, metricRepository, collectorGateway, eventPublisher)
        );
    }
}
