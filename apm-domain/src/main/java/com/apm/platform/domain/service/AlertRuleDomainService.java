package com.apm.platform.domain.service;

import com.apm.platform.domain.entity.AlertRule;
import com.apm.platform.domain.entity.Metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertRuleDomainService {

    private final Map<String, List<Boolean>> violationHistory = new HashMap<>();

    public boolean shouldTriggerAlert(AlertRule rule, List<Metric> recentMetrics) {
        if (rule == null || !rule.isEnabled()) {
            return false;
        }
        if (recentMetrics == null || recentMetrics.isEmpty()) {
            return false;
        }

        int requiredViolations = rule.getConsecutiveViolations();
        if (recentMetrics.size() < requiredViolations) {
            return false;
        }

        List<Metric> lastNMetrics = recentMetrics.subList(
                Math.max(0, recentMetrics.size() - requiredViolations),
                recentMetrics.size()
        );

        return lastNMetrics.stream().allMatch(rule::isViolated);
    }

    public boolean checkViolation(AlertRule rule, Metric metric) {
        if (rule == null || metric == null || !rule.isEnabled()) {
            return false;
        }
        return rule.isViolated(metric);
    }

    public void recordViolation(String ruleId, boolean violated) {
        if (ruleId == null || ruleId.isBlank()) {
            return;
        }

        violationHistory.computeIfAbsent(ruleId, k -> new ArrayList<>()).add(violated);

        List<Boolean> history = violationHistory.get(ruleId);
        if (history.size() > 100) {
            history.remove(0);
        }
    }

    public int getConsecutiveViolationCount(String ruleId) {
        List<Boolean> history = violationHistory.get(ruleId);
        if (history == null || history.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public void clearViolationHistory(String ruleId) {
        if (ruleId != null) {
            violationHistory.remove(ruleId);
        }
    }

    public double calculateViolationRate(String ruleId, int lastNChecks) {
        List<Boolean> history = violationHistory.get(ruleId);
        if (history == null || history.isEmpty()) {
            return 0.0;
        }

        int checksToConsider = Math.min(lastNChecks, history.size());
        List<Boolean> recentHistory = history.subList(
                history.size() - checksToConsider,
                history.size()
        );

        long violations = recentHistory.stream().filter(v -> v).count();
        return (violations * 100.0) / checksToConsider;
    }

    public boolean isFlapping(String ruleId, int windowSize) {
        List<Boolean> history = violationHistory.get(ruleId);
        if (history == null || history.size() < windowSize) {
            return false;
        }

        List<Boolean> window = history.subList(
                history.size() - windowSize,
                history.size()
        );

        int changes = 0;
        for (int i = 1; i < window.size(); i++) {
            if (!window.get(i).equals(window.get(i - 1))) {
                changes++;
            }
        }

        return changes >= (windowSize / 2);
    }

    public String evaluateRuleEffectiveness(String ruleId) {
        List<Boolean> history = violationHistory.get(ruleId);
        if (history == null || history.isEmpty()) {
            return "No data available";
        }

        double violationRate = calculateViolationRate(ruleId, history.size());
        boolean isFlapping = isFlapping(ruleId, Math.min(10, history.size()));

        if (isFlapping) {
            return "Rule may be too sensitive - frequent state changes detected";
        } else if (violationRate > 80) {
            return "Rule threshold may be too strict - high violation rate";
        } else if (violationRate < 5) {
            return "Rule threshold may be too lenient - very low violation rate";
        } else {
            return "Rule appears to be well-calibrated";
        }
    }
}
