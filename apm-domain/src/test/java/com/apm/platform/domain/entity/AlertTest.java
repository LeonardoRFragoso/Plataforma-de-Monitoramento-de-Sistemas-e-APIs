package com.apm.platform.domain.entity;

import com.apm.platform.domain.exception.InvalidSystemStateException;
import com.apm.platform.domain.valueobject.AlertSeverity;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AlertTest {

    @Test
    void shouldTriggerAlert() {
        Alert alert = Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING,
            "High latency detected");

        assertNotNull(alert.getId());
        assertEquals("system-123", alert.getSystemId());
        assertEquals("rule-456", alert.getRuleId());
        assertEquals(AlertSeverity.WARNING, alert.getSeverity());
        assertEquals("High latency detected", alert.getMessage());
        assertFalse(alert.isResolved());
        assertTrue(alert.isActive());
        assertNotNull(alert.getTriggeredAt());
    }

    @Test
    void shouldThrowExceptionWhenSystemIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            Alert.trigger(null, "rule-456", AlertSeverity.WARNING, "Message")
        );
    }

    @Test
    void shouldThrowExceptionWhenSeverityIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            Alert.trigger("system-123", "rule-456", null, "Message")
        );
    }

    @Test
    void shouldThrowExceptionWhenMessageIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING, null)
        );
    }

    @Test
    void shouldResolveAlert() {
        Alert alert = Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING,
            "High latency detected");

        alert.resolve("Issue fixed by restarting service");

        assertTrue(alert.isResolved());
        assertFalse(alert.isActive());
        assertNotNull(alert.getResolvedAt());
        assertEquals("Issue fixed by restarting service", alert.getResolutionNotes());
    }

    @Test
    void shouldThrowExceptionWhenResolvingAlreadyResolvedAlert() {
        Alert alert = Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING,
            "High latency detected");

        alert.resolve("Fixed");

        assertThrows(InvalidSystemStateException.class, () ->
            alert.resolve("Try to resolve again")
        );
    }

    @Test
    void shouldAutoResolveAlert() {
        Alert alert = Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING,
            "High latency detected");

        alert.autoResolve();

        assertTrue(alert.isResolved());
        assertTrue(alert.getResolutionNotes().contains("Auto-resolved"));
    }

    @Test
    void shouldIdentifyCriticalAlert() {
        Alert warning = Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING, "Message");
        Alert critical = Alert.trigger("system-123", "rule-456", AlertSeverity.CRITICAL, "Message");

        assertFalse(warning.isCritical());
        assertTrue(critical.isCritical());
    }

    @Test
    void shouldCalculateDuration() {
        Alert alert = Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING,
            "High latency detected");

        Duration duration = alert.getDuration();

        assertNotNull(duration);
        assertTrue(duration.getSeconds() >= 0);
    }

    @Test
    void shouldIdentifyLongRunningAlert() throws InterruptedException {
        Alert alert = Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING,
            "High latency detected");

        Thread.sleep(100);

        assertTrue(alert.isLongRunning(Duration.ofMillis(50)));
        assertFalse(alert.isLongRunning(Duration.ofSeconds(10)));
    }

    @Test
    void shouldBeEqualWhenSameId() {
        Alert alert1 = Alert.trigger("system-123", "rule-456", AlertSeverity.WARNING, "Message");
        Alert alert2 = Alert.reconstitute(
            alert1.getId(), "different-system", "different-rule", AlertSeverity.CRITICAL,
            "Different message", alert1.getTriggeredAt(), false, null, null
        );

        assertEquals(alert1, alert2);
        assertEquals(alert1.hashCode(), alert2.hashCode());
    }
}
