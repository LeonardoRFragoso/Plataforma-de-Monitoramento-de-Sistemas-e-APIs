package com.apm.platform.domain.service;

import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.valueobject.UptimePercentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UptimeCalculationDomainServiceTest {

    private UptimeCalculationDomainService service;

    @BeforeEach
    void setUp() {
        service = new UptimeCalculationDomainService();
    }

    @Test
    void shouldCalculateUptimeFromMetrics() {
        List<Metric> metrics = List.of(
            Metric.create("system-1", 500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 600, 200, false, 55.0, 65.0),
            Metric.create("system-1", 700, 500, true, 60.0, 70.0),
            Metric.create("system-1", 800, 200, false, 50.0, 60.0)
        );

        UptimePercentage uptime = service.calculateUptimeFromMetrics(metrics);

        assertEquals(75.0, uptime.getValue());
    }

    @Test
    void shouldReturnZeroUptimeForEmptyMetrics() {
        List<Metric> emptyMetrics = new ArrayList<>();

        UptimePercentage uptime = service.calculateUptimeFromMetrics(emptyMetrics);

        assertEquals(0.0, uptime.getValue());
    }

    @Test
    void shouldCalculateUptimeFromTimeRange() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        Duration downtime = Duration.ofMinutes(10);

        UptimePercentage uptime = service.calculateUptimeFromTimeRange(start, end, downtime);

        assertTrue(uptime.getValue() > 83.0 && uptime.getValue() < 84.0);
    }

    @Test
    void shouldReturnPerfectUptimeWhenNoDowntime() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();

        UptimePercentage uptime = service.calculateUptimeFromTimeRange(start, end, Duration.ZERO);

        assertEquals(100.0, uptime.getValue());
    }

    @Test
    void shouldThrowExceptionWhenEndBeforeStart() {
        Instant start = Instant.now();
        Instant end = start.minusSeconds(3600);

        assertThrows(IllegalArgumentException.class, () ->
            service.calculateUptimeFromTimeRange(start, end, Duration.ZERO)
        );
    }

    @Test
    void shouldCalculateAvailabilityRate() {
        double rate = service.calculateAvailabilityRate(95, 100);

        assertEquals(95.0, rate);
    }

    @Test
    void shouldReturnZeroAvailabilityWhenNoChecks() {
        double rate = service.calculateAvailabilityRate(0, 0);

        assertEquals(0.0, rate);
    }

    @Test
    void shouldEstimateDowntime() {
        List<Metric> metrics = List.of(
            Metric.create("system-1", 500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 600, 500, true, 55.0, 65.0),
            Metric.create("system-1", 700, 500, true, 60.0, 70.0)
        );

        Duration downtime = service.estimateDowntime(metrics, 60);

        assertEquals(120, downtime.getSeconds());
    }

    @Test
    void shouldCheckIfMeetsSLO() {
        UptimePercentage uptime = UptimePercentage.of(99.5);

        assertTrue(service.meetsServiceLevelObjective(uptime, 99.0));
        assertFalse(service.meetsServiceLevelObjective(uptime, 99.9));
    }

    @Test
    void shouldClassifyAvailability() {
        assertEquals("Four Nines (99.99%)", service.classifyAvailability(UptimePercentage.of(99.995)));
        assertEquals("High Availability", service.classifyAvailability(UptimePercentage.of(99.97)));
        assertEquals("Three Nines (99.9%)", service.classifyAvailability(UptimePercentage.of(99.92)));
        assertEquals("Two Nines (99%)", service.classifyAvailability(UptimePercentage.of(99.5)));
        assertEquals("Acceptable", service.classifyAvailability(UptimePercentage.of(97.0)));
        assertEquals("Poor", service.classifyAvailability(UptimePercentage.of(90.0)));
    }

    @Test
    void shouldCalculateAllowedDowntime() {
        Duration period = Duration.ofDays(30);
        Duration allowedDowntime = service.calculateAllowedDowntime(period, 99.9);

        long expectedSeconds = (long) (30 * 24 * 60 * 60 * 0.001);
        assertEquals(expectedSeconds, allowedDowntime.getSeconds());
    }

    @Test
    void shouldThrowExceptionWhenPeriodIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
            service.calculateAllowedDowntime(Duration.ZERO, 99.9)
        );
    }

    @Test
    void shouldThrowExceptionWhenInvalidSLO() {
        Duration period = Duration.ofDays(30);

        assertThrows(IllegalArgumentException.class, () ->
            service.calculateAllowedDowntime(period, -1)
        );

        assertThrows(IllegalArgumentException.class, () ->
            service.calculateAllowedDowntime(period, 101)
        );
    }
}
