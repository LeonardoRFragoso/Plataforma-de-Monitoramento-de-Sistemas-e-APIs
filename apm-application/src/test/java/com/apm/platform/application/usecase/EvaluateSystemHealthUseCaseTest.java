package com.apm.platform.application.usecase;

import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.outgoing.DomainEventPublisher;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.service.HealthEvaluationDomainService;
import com.apm.platform.domain.valueobject.SystemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvaluateSystemHealthUseCaseTest {

    private MonitoredSystemRepository systemRepository;
    private MetricRepository metricRepository;
    private HealthEvaluationDomainService healthService;
    private DomainEventPublisher eventPublisher;
    private EvaluateSystemHealthUseCase useCase;

    @BeforeEach
    void setUp() {
        systemRepository = mock(MonitoredSystemRepository.class);
        metricRepository = mock(MetricRepository.class);
        healthService = new HealthEvaluationDomainService();
        eventPublisher = mock(DomainEventPublisher.class);
        
        useCase = new EvaluateSystemHealthUseCase(
            systemRepository, metricRepository, healthService, eventPublisher
        );
    }

    @Test
    void shouldEvaluateHealthForSystem() {
        String systemId = "system-123";
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.test.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        List<Metric> metrics = List.of(
            Metric.create(systemId, 500, 200, false, 50.0, 60.0),
            Metric.create(systemId, 600, 200, false, 55.0, 65.0)
        );

        when(systemRepository.findById(systemId)).thenReturn(Optional.of(system));
        when(metricRepository.findRecentBySystemId(systemId, 10)).thenReturn(metrics);
        when(systemRepository.save(any(MonitoredSystem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SystemStatus result = useCase.execute(systemId);

        assertNotNull(result);
        assertEquals(SystemStatus.UP, result);

        verify(systemRepository, atLeastOnce()).findById(systemId);
        verify(metricRepository).findRecentBySystemId(systemId, 10);
    }

    @Test
    void shouldDetectDegradationAndPublishEvent() {
        String systemId = "system-123";
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.test.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        List<Metric> metrics = List.of(
            Metric.create(systemId, 5000, 200, false, 50.0, 60.0)
        );

        when(systemRepository.findById(systemId)).thenReturn(Optional.of(system));
        when(metricRepository.findRecentBySystemId(systemId, 10)).thenReturn(metrics);
        when(systemRepository.save(any(MonitoredSystem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SystemStatus result = useCase.execute(systemId);

        assertEquals(SystemStatus.DEGRADED, result);
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldNotPublishEventWhenStatusUnchanged() {
        String systemId = "system-123";
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.test.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        List<Metric> metrics = List.of(
            Metric.create(systemId, 500, 200, false, 50.0, 60.0)
        );

        when(systemRepository.findById(systemId)).thenReturn(Optional.of(system));
        when(metricRepository.findRecentBySystemId(systemId, 10)).thenReturn(metrics);

        SystemStatus result = useCase.execute(systemId);

        assertEquals(SystemStatus.UP, result);
        verify(eventPublisher, never()).publish(any());
        verify(systemRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSystemNotFound() {
        String systemId = "non-existent";

        when(systemRepository.findById(systemId)).thenReturn(Optional.empty());

        assertThrows(MonitoredSystemNotFoundException.class, () ->
            useCase.execute(systemId)
        );
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
            new EvaluateSystemHealthUseCase(null, metricRepository, healthService, eventPublisher)
        );
    }
}
