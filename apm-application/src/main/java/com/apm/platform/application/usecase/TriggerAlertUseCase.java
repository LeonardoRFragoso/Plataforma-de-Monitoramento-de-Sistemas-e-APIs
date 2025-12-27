package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.response.AlertResponse;
import com.apm.platform.application.mapper.AlertMapper;
import com.apm.platform.domain.entity.Alert;
import com.apm.platform.domain.entity.AlertRule;
import com.apm.platform.domain.event.AlertTriggeredEvent;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.incoming.TriggerAlert;
import com.apm.platform.domain.port.outgoing.AlertNotifierGateway;
import com.apm.platform.domain.port.outgoing.AlertRepository;
import com.apm.platform.domain.port.outgoing.AlertRuleRepository;
import com.apm.platform.domain.port.outgoing.DomainEventPublisher;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;

public class TriggerAlertUseCase implements TriggerAlert {

    private final MonitoredSystemRepository systemRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final AlertRepository alertRepository;
    private final AlertNotifierGateway notifierGateway;
    private final DomainEventPublisher eventPublisher;

    public TriggerAlertUseCase(MonitoredSystemRepository systemRepository,
                              AlertRuleRepository alertRuleRepository,
                              AlertRepository alertRepository,
                              AlertNotifierGateway notifierGateway,
                              DomainEventPublisher eventPublisher) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        if (alertRuleRepository == null) {
            throw new IllegalArgumentException("AlertRuleRepository cannot be null");
        }
        if (alertRepository == null) {
            throw new IllegalArgumentException("AlertRepository cannot be null");
        }
        if (notifierGateway == null) {
            throw new IllegalArgumentException("AlertNotifierGateway cannot be null");
        }
        if (eventPublisher == null) {
            throw new IllegalArgumentException("DomainEventPublisher cannot be null");
        }

        this.systemRepository = systemRepository;
        this.alertRuleRepository = alertRuleRepository;
        this.alertRepository = alertRepository;
        this.notifierGateway = notifierGateway;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Alert execute(String systemId, String ruleId, String message) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }

        systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        AlertRule rule = alertRuleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Alert rule not found: " + ruleId));

        Alert alert = Alert.trigger(systemId, ruleId, rule.getSeverity(), message);
        Alert savedAlert = alertRepository.save(alert);

        AlertTriggeredEvent event = AlertTriggeredEvent.create(
            savedAlert.getId(),
            savedAlert.getSystemId(),
            savedAlert.getRuleId(),
            savedAlert.getSeverity(),
            savedAlert.getMessage()
        );
        eventPublisher.publish(event);

        notifierGateway.notifyAlert(savedAlert);

        return savedAlert;
    }

    public AlertResponse executeAndReturnResponse(String systemId, String ruleId, String message) {
        Alert alert = execute(systemId, ruleId, message);
        return AlertMapper.toResponse(alert);
    }
}
