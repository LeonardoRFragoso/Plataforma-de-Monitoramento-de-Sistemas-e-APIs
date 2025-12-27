package com.apm.platform.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricSnapshotTest {

    @Test
    void shouldCreateValidSnapshot() {
        MetricSnapshot snapshot = MetricSnapshot.create(150, 45.5, 60.0, 200, false);

        assertEquals(150, snapshot.getLatencyMs());
        assertEquals(45.5, snapshot.getCpuUsagePercent());
        assertEquals(60.0, snapshot.getMemoryUsagePercent());
        assertEquals(200, snapshot.getStatusCode());
        assertFalse(snapshot.hasError());
        assertNotNull(snapshot.getTimestamp());
    }

    @Test
    void shouldThrowExceptionWhenLatencyIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            MetricSnapshot.create(-1, 50.0, 50.0, 200, false)
        );
    }

    @Test
    void shouldThrowExceptionWhenCpuUsageIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            MetricSnapshot.create(100, -1, 50.0, 200, false)
        );

        assertThrows(IllegalArgumentException.class, () ->
            MetricSnapshot.create(100, 101, 50.0, 200, false)
        );
    }

    @Test
    void shouldThrowExceptionWhenMemoryUsageIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            MetricSnapshot.create(100, 50.0, -1, 200, false)
        );

        assertThrows(IllegalArgumentException.class, () ->
            MetricSnapshot.create(100, 50.0, 101, 200, false)
        );
    }

    @Test
    void shouldIdentifySuccessfulResponse() {
        MetricSnapshot success = MetricSnapshot.create(100, 50.0, 50.0, 200, false);
        MetricSnapshot error = MetricSnapshot.create(100, 50.0, 50.0, 500, false);

        assertTrue(success.isSuccessfulResponse());
        assertFalse(error.isSuccessfulResponse());
    }

    @Test
    void shouldIdentifyHighLatency() {
        MetricSnapshot snapshot = MetricSnapshot.create(1500, 50.0, 50.0, 200, false);

        assertTrue(snapshot.isHighLatency(1000));
        assertFalse(snapshot.isHighLatency(2000));
    }

    @Test
    void shouldIdentifyHighCpuUsage() {
        MetricSnapshot snapshot = MetricSnapshot.create(100, 85.0, 50.0, 200, false);

        assertTrue(snapshot.isHighCpuUsage(80.0));
        assertFalse(snapshot.isHighCpuUsage(90.0));
    }

    @Test
    void shouldIdentifyHighMemoryUsage() {
        MetricSnapshot snapshot = MetricSnapshot.create(100, 50.0, 90.0, 200, false);

        assertTrue(snapshot.isHighMemoryUsage(85.0));
        assertFalse(snapshot.isHighMemoryUsage(95.0));
    }

    @Test
    void shouldBeEqualWhenSameValues() {
        MetricSnapshot snapshot1 = MetricSnapshot.create(100, 50.0, 50.0, 200, false);
        MetricSnapshot snapshot2 = MetricSnapshot.create(100, 50.0, 50.0, 200, false);

        assertNotEquals(snapshot1, snapshot2);
    }
}
