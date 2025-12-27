package com.apm.platform.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UptimePercentageTest {

    @Test
    void shouldCreateValidPercentage() {
        UptimePercentage uptime = UptimePercentage.of(99.5);

        assertEquals(99.5, uptime.getValue());
    }

    @Test
    void shouldThrowExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            UptimePercentage.of(-1.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenAbove100() {
        assertThrows(IllegalArgumentException.class, () -> 
            UptimePercentage.of(101.0)
        );
    }

    @Test
    void shouldCreateFromChecks() {
        UptimePercentage uptime = UptimePercentage.fromChecks(95, 100);

        assertEquals(95.0, uptime.getValue());
    }

    @Test
    void shouldThrowExceptionWhenTotalChecksIsZero() {
        assertThrows(IllegalArgumentException.class, () -> 
            UptimePercentage.fromChecks(0, 0)
        );
    }

    @Test
    void shouldThrowExceptionWhenSuccessfulChecksExceedsTotal() {
        assertThrows(IllegalArgumentException.class, () -> 
            UptimePercentage.fromChecks(101, 100)
        );
    }

    @Test
    void shouldCreateZeroUptime() {
        UptimePercentage uptime = UptimePercentage.zero();

        assertEquals(0.0, uptime.getValue());
    }

    @Test
    void shouldCreatePerfectUptime() {
        UptimePercentage uptime = UptimePercentage.perfect();

        assertEquals(100.0, uptime.getValue());
        assertTrue(uptime.isPerfect());
    }

    @Test
    void shouldIdentifyIfAboveThreshold() {
        UptimePercentage uptime = UptimePercentage.of(99.5);

        assertTrue(uptime.isAbove(99.0));
        assertFalse(uptime.isAbove(99.9));
    }

    @Test
    void shouldIdentifyIfBelowThreshold() {
        UptimePercentage uptime = UptimePercentage.of(99.5);

        assertTrue(uptime.isBelow(99.9));
        assertFalse(uptime.isBelow(99.0));
    }

    @Test
    void shouldIdentifyCriticalUptime() {
        UptimePercentage critical = UptimePercentage.of(94.0);
        UptimePercentage good = UptimePercentage.of(99.0);

        assertTrue(critical.isCritical());
        assertFalse(good.isCritical());
    }

    @Test
    void shouldIdentifyExcellentUptime() {
        UptimePercentage excellent = UptimePercentage.of(99.95);
        UptimePercentage good = UptimePercentage.of(99.0);

        assertTrue(excellent.isExcellent());
        assertFalse(good.isExcellent());
    }

    @Test
    void shouldFormatCorrectly() {
        UptimePercentage uptime = UptimePercentage.of(99.123);

        assertEquals("99.12%", uptime.toString());
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        UptimePercentage uptime1 = UptimePercentage.of(99.5);
        UptimePercentage uptime2 = UptimePercentage.of(99.5);

        assertEquals(uptime1, uptime2);
        assertEquals(uptime1.hashCode(), uptime2.hashCode());
    }
}
