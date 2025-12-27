package com.apm.platform.domain.entity;

import com.apm.platform.domain.exception.InvalidMetricDataException;
import com.apm.platform.domain.valueobject.MetricSnapshot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricTest {

    @Test
    void shouldCreateValidMetric() {
        Metric metric = Metric.create("system-123", 150, 200, false, 45.5, 60.0);

        assertNotNull(metric.getId());
        assertEquals("system-123", metric.getSystemId());
        assertEquals(150, metric.getLatencyMs());
        assertEquals(200, metric.getStatusCode());
        assertFalse(metric.hasError());
        assertEquals(45.5, metric.getCpuUsagePercent());
        assertEquals(60.0, metric.getMemoryUsagePercent());
        assertNotNull(metric.getCollectedAt());
    }

    @Test
    void shouldCreateFromSnapshot() {
        MetricSnapshot snapshot = MetricSnapshot.create(150, 45.5, 60.0, 200, false);
        Metric metric = Metric.fromSnapshot("system-123", snapshot);

        assertEquals("system-123", metric.getSystemId());
        assertEquals(150, metric.getLatencyMs());
        assertEquals(45.5, metric.getCpuUsagePercent());
    }

    @Test
    void shouldConvertToSnapshot() {
        Metric metric = Metric.create("system-123", 150, 200, false, 45.5, 60.0);
        MetricSnapshot snapshot = metric.toSnapshot();

        assertEquals(150, snapshot.getLatencyMs());
        assertEquals(45.5, snapshot.getCpuUsagePercent());
        assertEquals(60.0, snapshot.getMemoryUsagePercent());
    }

    @Test
    void shouldThrowExceptionWhenSystemIdIsNull() {
        assertThrows(InvalidMetricDataException.class, () ->
            Metric.create(null, 150, 200, false, 45.5, 60.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenSystemIdIsBlank() {
        assertThrows(InvalidMetricDataException.class, () ->
            Metric.create("", 150, 200, false, 45.5, 60.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenLatencyIsNegative() {
        assertThrows(InvalidMetricDataException.class, () ->
            Metric.create("system-123", -1, 200, false, 45.5, 60.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenLatencyExceedsMaximum() {
        assertThrows(InvalidMetricDataException.class, () ->
            Metric.create("system-123", 400000, 200, false, 45.5, 60.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenCpuUsageIsInvalid() {
        assertThrows(InvalidMetricDataException.class, () ->
            Metric.create("system-123", 150, 200, false, -1, 60.0)
        );

        assertThrows(InvalidMetricDataException.class, () ->
            Metric.create("system-123", 150, 200, false, 101, 60.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenMemoryUsageIsInvalid() {
        assertThrows(InvalidMetricDataException.class, () ->
            Metric.create("system-123", 150, 200, false, 45.5, -1)
        );

        assertThrows(InvalidMetricDataException.class, () ->
            Metric.create("system-123", 150, 200, false, 45.5, 101)
        );
    }

    @Test
    void shouldIdentifySuccessfulMetric() {
        Metric success = Metric.create("system-123", 150, 200, false, 45.5, 60.0);
        Metric error = Metric.create("system-123", 150, 200, true, 45.5, 60.0);
        Metric serverError = Metric.create("system-123", 150, 500, false, 45.5, 60.0);

        assertTrue(success.isSuccessful());
        assertFalse(error.isSuccessful());
        assertFalse(serverError.isSuccessful());
    }

    @Test
    void shouldIdentifyHighLatency() {
        Metric metric = Metric.create("system-123", 1500, 200, false, 45.5, 60.0);

        assertTrue(metric.isHighLatency(1000));
        assertFalse(metric.isHighLatency(2000));
    }

    @Test
    void shouldIdentifyServerError() {
        Metric success = Metric.create("system-123", 150, 200, false, 45.5, 60.0);
        Metric serverError = Metric.create("system-123", 150, 500, false, 45.5, 60.0);

        assertFalse(success.isServerError());
        assertTrue(serverError.isServerError());
    }

    @Test
    void shouldIdentifyClientError() {
        Metric success = Metric.create("system-123", 150, 200, false, 45.5, 60.0);
        Metric clientError = Metric.create("system-123", 150, 404, false, 45.5, 60.0);

        assertFalse(success.isClientError());
        assertTrue(clientError.isClientError());
    }

    @Test
    void shouldIdentifyDegradation() {
        Metric normalMetric = Metric.create("system-123", 500, 200, false, 45.5, 60.0);
        Metric highLatency = Metric.create("system-123", 1500, 200, false, 45.5, 60.0);
        Metric highCpu = Metric.create("system-123", 500, 200, false, 85.0, 60.0);
        Metric withError = Metric.create("system-123", 500, 200, true, 45.5, 60.0);

        assertFalse(normalMetric.indicatesDegradation(1000, 80.0));
        assertTrue(highLatency.indicatesDegradation(1000, 80.0));
        assertTrue(highCpu.indicatesDegradation(1000, 80.0));
        assertTrue(withError.indicatesDegradation(1000, 80.0));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        Metric metric1 = Metric.create("system-123", 150, 200, false, 45.5, 60.0);
        Metric metric2 = Metric.reconstitute(
            metric1.getId(), "different-system", 300, 500, true, 80.0, 90.0,
            metric1.getAdditionalData(), metric1.getCollectedAt()
        );

        assertEquals(metric1, metric2);
        assertEquals(metric1.hashCode(), metric2.hashCode());
    }
}
