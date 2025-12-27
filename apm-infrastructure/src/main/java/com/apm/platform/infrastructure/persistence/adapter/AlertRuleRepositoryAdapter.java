package com.apm.platform.infrastructure.persistence.adapter;

import com.apm.platform.domain.entity.AlertRule;
import com.apm.platform.domain.port.outgoing.AlertRuleRepository;
import com.apm.platform.domain.valueobject.AlertSeverity;
import com.apm.platform.infrastructure.persistence.entity.AlertRuleEntity;
import com.apm.platform.infrastructure.persistence.repository.AlertRuleJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AlertRuleRepositoryAdapter implements AlertRuleRepository {

    private final AlertRuleJpaRepository jpaRepository;

    public AlertRuleRepositoryAdapter(AlertRuleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AlertRule save(AlertRule rule) {
        AlertRuleEntity entity = toEntity(rule);
        AlertRuleEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AlertRule> findById(String ruleId) {
        return jpaRepository.findById(ruleId).map(this::toDomain);
    }

    @Override
    public List<AlertRule> findBySystemId(String systemId) {
        return jpaRepository.findBySystemId(systemId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertRule> findEnabledBySystemId(String systemId) {
        return jpaRepository.findEnabledBySystemId(systemId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertRule> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String ruleId) {
        jpaRepository.deleteById(ruleId);
    }

    private AlertRuleEntity toEntity(AlertRule domain) {
        AlertRuleEntity entity = new AlertRuleEntity();
        entity.setId(domain.getId());
        entity.setSystemId(domain.getSystemId());
        entity.setName(domain.getName());
        entity.setType(AlertRuleEntity.AlertRuleType.valueOf(domain.getType().name()));
        entity.setSeverity(AlertRuleEntity.AlertSeverity.valueOf(domain.getSeverity().name()));
        entity.setThresholdValue(domain.getThresholdValue());
        entity.setConsecutiveViolations(domain.getConsecutiveViolations());
        entity.setEnabled(domain.isEnabled());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private AlertRule toDomain(AlertRuleEntity entity) {
        return AlertRule.reconstitute(
            entity.getId(),
            entity.getSystemId(),
            entity.getName(),
            AlertRule.AlertRuleType.valueOf(entity.getType().name()),
            AlertSeverity.valueOf(entity.getSeverity().name()),
            entity.getThresholdValue(),
            entity.getConsecutiveViolations(),
            entity.isEnabled(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
