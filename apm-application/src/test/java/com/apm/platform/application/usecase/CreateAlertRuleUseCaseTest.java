package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.request.CreateAlertRuleRequest;
import com.apm.platform.application.dto.response.AlertRuleResponse;
import com.apm.platform.domain.entity.AlertRule;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.outgoing.AlertRuleRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.valueobject.AlertSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateAlertRuleUseCaseTest {

    private MonitoredSystemRepository systemRepository;
    private AlertRuleRepository alertRuleRepository;
    private CreateAlertRuleUseCase useCase;

    @BeforeEach
    void setUp() {
        systemRepository = mock(MonitoredSystemRepository.class);
        alertRuleRepository = mock(AlertRuleRepository.class);
        useCase = new CreateAlertRuleUseCase(systemRepository, alertRuleRepository);
    }

    @Test
    void shouldCreateAlertRule() {
        String systemId = "system-123";
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.test.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        when(systemRepository.findById(systemId)).thenReturn(Optional.of(system));
        when(alertRuleRepository.save(any(AlertRule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlertRule result = useCase.execute(
            systemId, "High Latency", AlertRule.AlertRuleType.LATENCY_MS,
            AlertSeverity.WARNING, 1000.0, 2
        );

        assertNotNull(result);
        assertEquals(systemId, result.getSystemId());
        assertEquals("High Latency", result.getName());
        assertEquals(AlertRule.AlertRuleType.LATENCY_MS, result.getType());
        assertEquals(AlertSeverity.WARNING, result.getSeverity());
        assertEquals(1000.0, result.getThresholdValue());
        assertEquals(2, result.getConsecutiveViolations());
        assertTrue(result.isEnabled());

        verify(systemRepository).findById(systemId);
        verify(alertRuleRepository).save(any(AlertRule.class));
    }

    @Test
    void shouldCreateAlertRuleUsingRequest() {
        String systemId = "system-123";
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.test.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        CreateAlertRuleRequest request = new CreateAlertRuleRequest(
            systemId, "High CPU", "CPU_USAGE_PERCENT", "CRITICAL", 80.0, 3
        );

        when(systemRepository.findById(systemId)).thenReturn(Optional.of(system));
        when(alertRuleRepository.save(any(AlertRule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlertRuleResponse response = useCase.execute(request);

        assertNotNull(response);
        assertEquals(systemId, response.getSystemId());
        assertEquals("High CPU", response.getName());
        assertEquals("CPU_USAGE_PERCENT", response.getType());
        assertEquals("CRITICAL", response.getSeverity());
    }

    @Test
    void shouldThrowExceptionWhenSystemNotFound() {
        String systemId = "non-existent";

        when(systemRepository.findById(systemId)).thenReturn(Optional.empty());

        assertThrows(MonitoredSystemNotFoundException.class, () ->
            useCase.execute(systemId, "Rule", AlertRule.AlertRuleType.LATENCY_MS,
                AlertSeverity.WARNING, 1000.0, 2)
        );

        verify(alertRuleRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRequestIsInvalid() {
        CreateAlertRuleRequest invalidRequest = new CreateAlertRuleRequest(
            "", "Rule", "LATENCY_MS", "WARNING", 1000.0, 2
        );

        assertThrows(IllegalArgumentException.class, () ->
            useCase.execute(invalidRequest)
        );

        verify(alertRuleRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new CreateAlertRuleUseCase(null, alertRuleRepository)
        );
    }
}
