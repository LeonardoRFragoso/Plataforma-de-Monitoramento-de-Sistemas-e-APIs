package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.request.CreateAlertRuleRequest;
import com.apm.platform.application.dto.response.AlertRuleResponse;
import com.apm.platform.application.mapper.AlertMapper;
import com.apm.platform.application.mapper.AlertRuleMapper;
import com.apm.platform.domain.entity.AlertRule;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.incoming.CreateAlertRule;
import com.apm.platform.domain.port.outgoing.AlertRuleRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.valueobject.AlertSeverity;

import java.util.List;

public class CreateAlertRuleUseCase implements CreateAlertRule {

    private final MonitoredSystemRepository systemRepository;
    private final AlertRuleRepository alertRuleRepository;

    public CreateAlertRuleUseCase(MonitoredSystemRepository systemRepository,
                                 AlertRuleRepository alertRuleRepository) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        if (alertRuleRepository == null) {
            throw new IllegalArgumentException("AlertRuleRepository cannot be null");
        }

        this.systemRepository = systemRepository;
        this.alertRuleRepository = alertRuleRepository;
    }

    @Override
    public AlertRule execute(String systemId, String name, AlertRule.AlertRuleType type,
                            AlertSeverity severity, double thresholdValue, int consecutiveViolations) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }

        systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        AlertRule rule = AlertRule.create(
            systemId,
            name,
            type,
            severity,
            thresholdValue,
            consecutiveViolations
        );

        return alertRuleRepository.save(rule);
    }

    public AlertRuleResponse execute(CreateAlertRuleRequest request) {
        List<String> validationErrors = request.validate();
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", validationErrors));
        }

        AlertRule.AlertRuleType type = AlertRuleMapper.parseRuleType(request.getType());
        AlertSeverity severity = AlertMapper.parseSeverity(request.getSeverity());

        AlertRule rule = execute(
            request.getSystemId(),
            request.getName(),
            type,
            severity,
            request.getThresholdValue(),
            request.getConsecutiveViolations()
        );

        return AlertRuleMapper.toResponse(rule);
    }
}
