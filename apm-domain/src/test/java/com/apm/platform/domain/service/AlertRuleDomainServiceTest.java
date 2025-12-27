package com.apm.platform.domain.service;

import com.apm.platform.domain.entity.AlertRule;
import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.valueobject.AlertSeverity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertRuleDomainServiceTest {

    private AlertRuleDomainService service;

    @BeforeEach
    void setUp() {
        service = new AlertRuleDomainService();
    }

    @Test
    void shouldNotTriggerAlertWhenRuleIsDisabled() {
        AlertRule rule = AlertRule.create("system-1", "High Latency",
            AlertRule.AlertRuleType.LATENCY_MS, AlertSeverity.WARNING, 1000, 2);
        rule.disable();

        List<Metric> metrics = List.of(
            Metric.create("system-1", 1500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 1600, 200, false, 50.0, 60.0)
        );

        assertFalse(service.shouldTriggerAlert(rule, metrics));
    }

    @Test
    void shouldNotTriggerAlertWhenNotEnoughMetrics() {
        AlertRule rule = AlertRule.create("system-1", "High Latency",
            AlertRule.AlertRuleType.LATENCY_MS, AlertSeverity.WARNING, 1000, 3);

        List<Metric> metrics = List.of(
            Metric.create("system-1", 1500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 1600, 200, false, 50.0, 60.0)
        );

        assertFalse(service.shouldTriggerAlert(rule, metrics));
    }

    @Test
    void shouldTriggerAlertWhenConsecutiveViolations() {
        AlertRule rule = AlertRule.create("system-1", "High Latency",
            AlertRule.AlertRuleType.LATENCY_MS, AlertSeverity.WARNING, 1000, 2);

        List<Metric> metrics = List.of(
            Metric.create("system-1", 500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 1500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 1600, 200, false, 50.0, 60.0)
        );

        assertTrue(service.shouldTriggerAlert(rule, metrics));
    }

    @Test
    void shouldNotTriggerAlertWhenViolationsNotConsecutive() {
        AlertRule rule = AlertRule.create("system-1", "High Latency",
            AlertRule.AlertRuleType.LATENCY_MS, AlertSeverity.WARNING, 1000, 2);

        List<Metric> metrics = List.of(
            Metric.create("system-1", 1500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 500, 200, false, 50.0, 60.0),
            Metric.create("system-1", 1600, 200, false, 50.0, 60.0)
        );

        assertFalse(service.shouldTriggerAlert(rule, metrics));
    }

    @Test
    void shouldCheckViolationForLatencyRule() {
        AlertRule rule = AlertRule.create("system-1", "High Latency",
            AlertRule.AlertRuleType.LATENCY_MS, AlertSeverity.WARNING, 1000, 1);

        Metric violatingMetric = Metric.create("system-1", 1500, 200, false, 50.0, 60.0);
        Metric normalMetric = Metric.create("system-1", 500, 200, false, 50.0, 60.0);

        assertTrue(service.checkViolation(rule, violatingMetric));
        assertFalse(service.checkViolation(rule, normalMetric));
    }

    @Test
    void shouldCheckViolationForCpuRule() {
        AlertRule rule = AlertRule.create("system-1", "High CPU",
            AlertRule.AlertRuleType.CPU_USAGE_PERCENT, AlertSeverity.WARNING, 80, 1);

        Metric violatingMetric = Metric.create("system-1", 500, 200, false, 85.0, 60.0);
        Metric normalMetric = Metric.create("system-1", 500, 200, false, 50.0, 60.0);

        assertTrue(service.checkViolation(rule, violatingMetric));
        assertFalse(service.checkViolation(rule, normalMetric));
    }

    @Test
    void shouldRecordViolationHistory() {
        String ruleId = "rule-123";

        service.recordViolation(ruleId, true);
        service.recordViolation(ruleId, true);
        service.recordViolation(ruleId, false);

        assertEquals(2, service.getConsecutiveViolationCount(ruleId));
    }

    @Test
    void shouldCalculateViolationRate() {
        String ruleId = "rule-123";

        service.recordViolation(ruleId, true);
        service.recordViolation(ruleId, true);
        service.recordViolation(ruleId, false);
        service.recordViolation(ruleId, true);

        double rate = service.calculateViolationRate(ruleId, 4);

        assertEquals(75.0, rate);
    }

    @Test
    void shouldDetectFlapping() {
        String ruleId = "rule-123";

        for (int i = 0; i < 10; i++) {
            service.recordViolation(ruleId, i % 2 == 0);
        }

        assertTrue(service.isFlapping(ruleId, 10));
    }

    @Test
    void shouldNotDetectFlappingWhenStable() {
        String ruleId = "rule-123";

        for (int i = 0; i < 10; i++) {
            service.recordViolation(ruleId, true);
        }

        assertFalse(service.isFlapping(ruleId, 10));
    }

    @Test
    void shouldEvaluateRuleEffectiveness() {
        String ruleId = "rule-123";

        for (int i = 0; i < 20; i++) {
            service.recordViolation(ruleId, i < 18);
        }

        String evaluation = service.evaluateRuleEffectiveness(ruleId);

        assertTrue(evaluation.contains("too strict"));
    }

    @Test
    void shouldClearViolationHistory() {
        String ruleId = "rule-123";

        service.recordViolation(ruleId, true);
        service.recordViolation(ruleId, true);

        assertEquals(2, service.getConsecutiveViolationCount(ruleId));

        service.clearViolationHistory(ruleId);

        assertEquals(0, service.getConsecutiveViolationCount(ruleId));
    }
}
