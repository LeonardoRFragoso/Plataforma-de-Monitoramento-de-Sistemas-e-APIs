package com.apm.platform.domain.port.outgoing;

import com.apm.platform.domain.entity.AlertRule;

import java.util.List;
import java.util.Optional;

public interface AlertRuleRepository {
    AlertRule save(AlertRule rule);
    Optional<AlertRule> findById(String ruleId);
    List<AlertRule> findBySystemId(String systemId);
    List<AlertRule> findEnabledBySystemId(String systemId);
    List<AlertRule> findAll();
    void deleteById(String ruleId);
}
