package com.apm.platform.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LatencyThresholdTest {

    @Test
    void shouldCreateValidThreshold() {
        LatencyThreshold threshold = LatencyThreshold.create(1000, 3000);

        assertEquals(1000, threshold.getWarningMs());
        assertEquals(3000, threshold.getCriticalMs());
    }

    @Test
    void shouldCreateDefaultThresholds() {
        LatencyThreshold threshold = LatencyThreshold.defaultThresholds();

        assertEquals(1000, threshold.getWarningMs());
        assertEquals(3000, threshold.getCriticalMs());
    }

    @Test
    void shouldThrowExceptionWhenWarningIsZero() {
        assertThrows(IllegalArgumentException.class, () -> 
            LatencyThreshold.create(0, 3000)
        );
    }

    @Test
    void shouldThrowExceptionWhenCriticalIsZero() {
        assertThrows(IllegalArgumentException.class, () -> 
            LatencyThreshold.create(1000, 0)
        );
    }

    @Test
    void shouldThrowExceptionWhenCriticalIsLessThanWarning() {
        assertThrows(IllegalArgumentException.class, () -> 
            LatencyThreshold.create(3000, 1000)
        );
    }

    @Test
    void shouldIdentifyWarningLatency() {
        LatencyThreshold threshold = LatencyThreshold.create(1000, 3000);

        assertTrue(threshold.isWarning(1500));
        assertTrue(threshold.isWarning(2999));
    }

    @Test
    void shouldIdentifyCriticalLatency() {
        LatencyThreshold threshold = LatencyThreshold.create(1000, 3000);

        assertTrue(threshold.isCritical(3001));
        assertTrue(threshold.isCritical(5000));
    }

    @Test
    void shouldIdentifyNormalLatency() {
        LatencyThreshold threshold = LatencyThreshold.create(1000, 3000);

        assertTrue(threshold.isNormal(500));
        assertTrue(threshold.isNormal(1000));
    }

    @Test
    void shouldEvaluateSeverityCorrectly() {
        LatencyThreshold threshold = LatencyThreshold.create(1000, 3000);

        assertNull(threshold.evaluateSeverity(800));
        assertEquals(AlertSeverity.WARNING, threshold.evaluateSeverity(1500));
        assertEquals(AlertSeverity.CRITICAL, threshold.evaluateSeverity(4000));
    }

    @Test
    void shouldBeEqualWhenSameValues() {
        LatencyThreshold threshold1 = LatencyThreshold.create(1000, 3000);
        LatencyThreshold threshold2 = LatencyThreshold.create(1000, 3000);

        assertEquals(threshold1, threshold2);
        assertEquals(threshold1.hashCode(), threshold2.hashCode());
    }
}
