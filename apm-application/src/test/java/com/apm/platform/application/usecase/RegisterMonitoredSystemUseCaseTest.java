package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.request.RegisterMonitoredSystemRequest;
import com.apm.platform.application.dto.response.MonitoredSystemResponse;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.DuplicateSystemException;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterMonitoredSystemUseCaseTest {

    private MonitoredSystemRepository systemRepository;
    private RegisterMonitoredSystemUseCase useCase;

    @BeforeEach
    void setUp() {
        systemRepository = mock(MonitoredSystemRepository.class);
        useCase = new RegisterMonitoredSystemUseCase(systemRepository);
    }

    @Test
    void shouldRegisterNewSystem() {
        String name = "Test API";
        String baseUrl = "https://api.test.com";
        MonitoredSystem.SystemType type = MonitoredSystem.SystemType.API;
        MonitoredSystem.Environment environment = MonitoredSystem.Environment.PRODUCTION;
        int interval = 60;

        when(systemRepository.existsByName(name)).thenReturn(false);
        when(systemRepository.save(any(MonitoredSystem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MonitoredSystem result = useCase.execute(name, baseUrl, type, environment, interval);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(baseUrl, result.getBaseUrl());
        assertEquals(type, result.getType());
        assertEquals(environment, result.getEnvironment());
        assertEquals(interval, result.getCollectionIntervalSeconds());
        assertTrue(result.isActive());

        verify(systemRepository).existsByName(name);
        verify(systemRepository).save(any(MonitoredSystem.class));
    }

    @Test
    void shouldThrowExceptionWhenSystemNameAlreadyExists() {
        String name = "Existing API";

        when(systemRepository.existsByName(name)).thenReturn(true);

        assertThrows(DuplicateSystemException.class, () ->
            useCase.execute(name, "https://api.test.com", 
                MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60)
        );

        verify(systemRepository).existsByName(name);
        verify(systemRepository, never()).save(any(MonitoredSystem.class));
    }

    @Test
    void shouldRegisterSystemUsingRequest() {
        RegisterMonitoredSystemRequest request = new RegisterMonitoredSystemRequest(
            "Test API", "https://api.test.com", "API", "PRODUCTION", 60
        );

        when(systemRepository.existsByName(anyString())).thenReturn(false);
        when(systemRepository.save(any(MonitoredSystem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MonitoredSystemResponse response = useCase.execute(request);

        assertNotNull(response);
        assertEquals("Test API", response.getName());
        assertEquals("https://api.test.com", response.getBaseUrl());
        assertEquals("API", response.getType());
        assertEquals("PRODUCTION", response.getEnvironment());
        assertEquals(60, response.getCollectionIntervalSeconds());
    }

    @Test
    void shouldThrowExceptionWhenRequestIsInvalid() {
        RegisterMonitoredSystemRequest invalidRequest = new RegisterMonitoredSystemRequest(
            "", "https://api.test.com", "API", "PRODUCTION", 60
        );

        assertThrows(IllegalArgumentException.class, () ->
            useCase.execute(invalidRequest)
        );

        verify(systemRepository, never()).save(any(MonitoredSystem.class));
    }

    @Test
    void shouldThrowExceptionWhenRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new RegisterMonitoredSystemUseCase(null)
        );
    }
}
