package com.apm.platform.application.mapper;

import com.apm.platform.application.dto.response.AlertRuleResponse;
import com.apm.platform.domain.entity.AlertRule;

public class AlertRuleMapper {

    public static AlertRuleResponse toResponse(AlertRule rule) {
        if (rule == null) {
            return null;
        }

        return new AlertRuleResponse(
            rule.getId(),
            rule.getSystemId(),
            rule.getName(),
            rule.getType().name(),
            rule.getSeverity().name(),
            rule.getThresholdValue(),
            rule.getConsecutiveViolations(),
            rule.isEnabled(),
            rule.getCreatedAt(),
            rule.getUpdatedAt()
        );
    }

    public static AlertRule.AlertRuleType parseRuleType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Rule type cannot be null or blank");
        }
        try {
            return AlertRule.AlertRuleType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid rule type: " + type + 
                ". Valid values are: LATENCY_MS, ERROR_RATE_PERCENT, CPU_USAGE_PERCENT, MEMORY_USAGE_PERCENT, STATUS_CODE");
        }
    }
}
