package com.apm.platform.domain.service;

import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.valueobject.HealthCheckResult;
import com.apm.platform.domain.valueobject.LatencyThreshold;
import com.apm.platform.domain.valueobject.SystemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HealthEvaluationDomainServiceTest {

    private HealthEvaluationDomainService service;
    private LatencyThreshold threshold;

    @BeforeEach
    void setUp() {
        service = new HealthEvaluationDomainService();
        threshold = LatencyThreshold.create(1000, 3000);
    }

    @Test
    void shouldReturnUpWhenNoMetrics() {
        List<Metric> emptyMetrics = new ArrayList<>();

        SystemStatus status = service.evaluateHealthFromMetrics(emptyMetrics, threshold);

        assertEquals(SystemStatus.UP, status);
    }

    @Test
    void shouldReturnUpForHealthyMetrics() {
        List<Metric> metrics = List.of(
            Metric.create("system-1", 500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 600, 200, false, 55.0, 65.0)
        );

        SystemStatus status = service.evaluateHealthFromMetrics(metrics, threshold);

        assertEquals(SystemStatus.UP, status);
    }

    @Test
    void shouldReturnDownWhenConsecutiveErrors() {
        List<Metric> metrics = List.of(
            Metric.create("system-1", 500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 500, 500, true, 50.0, 60.0),
            Metric.create("system-1", 500, 500, true, 50.0, 60.0),
            Metric.create("system-1", 500, 500, true, 50.0, 60.0)
        );

        SystemStatus status = service.evaluateHealthFromMetrics(metrics, threshold);

        assertEquals(SystemStatus.DOWN, status);
    }

    @Test
    void shouldReturnDegradedForHighLatency() {
        List<Metric> metrics = List.of(
            Metric.create("system-1", 4000, 200, false, 50.0, 60.0)
        );

        SystemStatus status = service.evaluateHealthFromMetrics(metrics, threshold);

        assertEquals(SystemStatus.DEGRADED, status);
    }

    @Test
    void shouldReturnDegradedForHighCpuUsage() {
        List<Metric> metrics = List.of(
            Metric.create("system-1", 500, 200, false, 85.0, 60.0)
        );

        SystemStatus status = service.evaluateHealthFromMetrics(metrics, threshold);

        assertEquals(SystemStatus.DEGRADED, status);
    }

    @Test
    void shouldReturnDegradedForHighMemoryUsage() {
        List<Metric> metrics = List.of(
            Metric.create("system-1", 500, 200, false, 50.0, 90.0)
        );

        SystemStatus status = service.evaluateHealthFromMetrics(metrics, threshold);

        assertEquals(SystemStatus.DEGRADED, status);
    }

    @Test
    void shouldEvaluateFromHealthCheckResult() {
        HealthCheckResult upResult = HealthCheckResult.healthy(100, 200);
        HealthCheckResult degradedResult = HealthCheckResult.degraded(2000, 200, "Slow response");
        HealthCheckResult downResult = HealthCheckResult.down("Connection timeout");

        assertEquals(SystemStatus.UP, service.evaluateHealthFromCheckResult(upResult));
        assertEquals(SystemStatus.DEGRADED, service.evaluateHealthFromCheckResult(degradedResult));
        assertEquals(SystemStatus.DOWN, service.evaluateHealthFromCheckResult(downResult));
    }

    @Test
    void shouldDetectStatusChange() {
        assertTrue(service.hasStatusChanged(SystemStatus.UP, SystemStatus.DEGRADED));
        assertFalse(service.hasStatusChanged(SystemStatus.UP, SystemStatus.UP));
        assertFalse(service.hasStatusChanged(null, SystemStatus.UP));
    }

    @Test
    void shouldIdentifySignificantDegradation() {
        assertTrue(service.isSignificantDegradation(SystemStatus.UP, SystemStatus.DEGRADED));
        assertTrue(service.isSignificantDegradation(SystemStatus.UP, SystemStatus.DOWN));
        assertTrue(service.isSignificantDegradation(SystemStatus.DEGRADED, SystemStatus.DOWN));
        assertFalse(service.isSignificantDegradation(SystemStatus.DOWN, SystemStatus.DEGRADED));
    }

    @Test
    void shouldIdentifyRecovery() {
        assertTrue(service.isRecovery(SystemStatus.DOWN, SystemStatus.DEGRADED));
        assertTrue(service.isRecovery(SystemStatus.DOWN, SystemStatus.UP));
        assertTrue(service.isRecovery(SystemStatus.DEGRADED, SystemStatus.UP));
        assertFalse(service.isRecovery(SystemStatus.UP, SystemStatus.DEGRADED));
    }

    @Test
    void shouldGenerateHealthSummary() {
        List<Metric> metrics = List.of(
            Metric.create("system-1", 500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 600, 200, false, 55.0, 65.0),
            Metric.create("system-1", 700, 500, true, 60.0, 70.0)
        );

        String summary = service.generateHealthSummary(SystemStatus.DEGRADED, metrics);

        assertNotNull(summary);
        assertTrue(summary.contains("DEGRADED"));
        assertTrue(summary.contains("1/3"));
    }
}
