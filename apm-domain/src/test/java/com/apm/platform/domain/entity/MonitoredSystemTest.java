package com.apm.platform.domain.entity;

import com.apm.platform.domain.exception.InvalidSystemStateException;
import com.apm.platform.domain.valueobject.SystemStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonitoredSystemTest {

    @Test
    void shouldCreateValidSystem() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API",
            "https://api.example.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        assertNotNull(system.getId());
        assertEquals("Test API", system.getName());
        assertEquals("https://api.example.com", system.getBaseUrl());
        assertEquals(MonitoredSystem.SystemType.API, system.getType());
        assertEquals(MonitoredSystem.Environment.PRODUCTION, system.getEnvironment());
        assertEquals(60, system.getCollectionIntervalSeconds());
        assertTrue(system.isActive());
        assertEquals(SystemStatus.UP, system.getCurrentStatus());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            MonitoredSystem.create(null, "https://api.example.com",
                MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60)
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
            MonitoredSystem.create("", "https://api.example.com",
                MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60)
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsTooLong() {
        String longName = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () ->
            MonitoredSystem.create(longName, "https://api.example.com",
                MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60)
        );
    }

    @Test
    void shouldThrowExceptionWhenBaseUrlIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            MonitoredSystem.create("Test API", "invalid-url",
                MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60)
        );
    }

    @Test
    void shouldThrowExceptionWhenIntervalIsTooShort() {
        assertThrows(IllegalArgumentException.class, () ->
            MonitoredSystem.create("Test API", "https://api.example.com",
                MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 5)
        );
    }

    @Test
    void shouldThrowExceptionWhenIntervalIsTooLong() {
        assertThrows(IllegalArgumentException.class, () ->
            MonitoredSystem.create("Test API", "https://api.example.com",
                MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 90000)
        );
    }

    @Test
    void shouldUpdateSystemDetails() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        system.updateDetails("Updated API", "https://api2.example.com",
            MonitoredSystem.SystemType.MICROSERVICE, MonitoredSystem.Environment.STAGING, 120);

        assertEquals("Updated API", system.getName());
        assertEquals("https://api2.example.com", system.getBaseUrl());
        assertEquals(MonitoredSystem.SystemType.MICROSERVICE, system.getType());
        assertEquals(MonitoredSystem.Environment.STAGING, system.getEnvironment());
        assertEquals(120, system.getCollectionIntervalSeconds());
    }

    @Test
    void shouldUpdateStatus() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        system.updateStatus(SystemStatus.DEGRADED);

        assertEquals(SystemStatus.DEGRADED, system.getCurrentStatus());
        assertNotNull(system.getLastCheckAt());
    }

    @Test
    void shouldDeactivateSystem() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        system.deactivate();

        assertFalse(system.isActive());
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingInactiveSystem() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        system.deactivate();

        assertThrows(InvalidSystemStateException.class, system::deactivate);
    }

    @Test
    void shouldActivateSystem() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        system.deactivate();
        system.activate();

        assertTrue(system.isActive());
    }

    @Test
    void shouldThrowExceptionWhenActivatingActiveSystem() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        assertThrows(InvalidSystemStateException.class, system::activate);
    }

    @Test
    void shouldIdentifyHealthySystem() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        assertTrue(system.isHealthy());

        system.updateStatus(SystemStatus.DEGRADED);
        assertFalse(system.isHealthy());
    }

    @Test
    void shouldIdentifyOperationalSystem() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        assertTrue(system.isOperational());

        system.updateStatus(SystemStatus.DOWN);
        assertFalse(system.isOperational());

        system.updateStatus(SystemStatus.DEGRADED);
        assertTrue(system.isOperational());

        system.deactivate();
        assertFalse(system.isOperational());
    }

    @Test
    void shouldCollectMetricsWhenActive() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        assertTrue(system.shouldCollectMetrics());

        system.deactivate();
        assertFalse(system.shouldCollectMetrics());
    }

    @Test
    void shouldBeEqualWhenSameId() {
        MonitoredSystem system1 = MonitoredSystem.create(
            "Test API", "https://api.example.com",
            MonitoredSystem.SystemType.API, MonitoredSystem.Environment.PRODUCTION, 60
        );

        MonitoredSystem system2 = MonitoredSystem.reconstitute(
            system1.getId(), "Different Name", "https://different.com",
            MonitoredSystem.SystemType.SERVICE, MonitoredSystem.Environment.DEVELOPMENT, 120,
            true, SystemStatus.DOWN, null, system1.getCreatedAt(), system1.getUpdatedAt()
        );

        assertEquals(system1, system2);
        assertEquals(system1.hashCode(), system2.hashCode());
    }
}
