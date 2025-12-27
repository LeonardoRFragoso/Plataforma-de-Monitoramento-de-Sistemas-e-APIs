package com.apm.platform.domain.port.incoming;

import com.apm.platform.domain.entity.AlertRule;
import com.apm.platform.domain.valueobject.AlertSeverity;

public interface CreateAlertRule {
    AlertRule execute(String systemId, String name, AlertRule.AlertRuleType type,
                     AlertSeverity severity, double thresholdValue, int consecutiveViolations);
}
