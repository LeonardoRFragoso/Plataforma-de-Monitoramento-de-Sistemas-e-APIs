package com.apm.platform.infrastructure.persistence.adapter;

import com.apm.platform.domain.entity.Alert;
import com.apm.platform.domain.port.outgoing.AlertRepository;
import com.apm.platform.domain.valueobject.AlertSeverity;
import com.apm.platform.infrastructure.persistence.entity.AlertEntity;
import com.apm.platform.infrastructure.persistence.repository.AlertJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AlertRepositoryAdapter implements AlertRepository {

    private final AlertJpaRepository jpaRepository;

    public AlertRepositoryAdapter(AlertJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Alert save(Alert alert) {
        AlertEntity entity = toEntity(alert);
        AlertEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Alert> findById(String alertId) {
        return jpaRepository.findById(alertId).map(this::toDomain);
    }

    @Override
    public List<Alert> findBySystemId(String systemId) {
        return jpaRepository.findBySystemIdOrderByTriggeredAtDesc(systemId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findActiveBySystemId(String systemId) {
        return jpaRepository.findActiveBySystemId(systemId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findBySeverity(AlertSeverity severity) {
        AlertEntity.AlertSeverity entitySeverity = AlertEntity.AlertSeverity.valueOf(severity.name());
        return jpaRepository.findBySeverity(entitySeverity).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findBySystemIdAndTimeRange(String systemId, Instant startTime, Instant endTime) {
        return jpaRepository.findBySystemIdAndTriggeredAtBetween(systemId, startTime, endTime).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alert> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countActiveBySystemId(String systemId) {
        return jpaRepository.countBySystemIdAndResolved(systemId, false);
    }

    private AlertEntity toEntity(Alert domain) {
        AlertEntity entity = new AlertEntity();
        entity.setId(domain.getId());
        entity.setSystemId(domain.getSystemId());
        entity.setRuleId(domain.getRuleId());
        entity.setSeverity(AlertEntity.AlertSeverity.valueOf(domain.getSeverity().name()));
        entity.setMessage(domain.getMessage());
        entity.setTriggeredAt(domain.getTriggeredAt());
        entity.setResolved(domain.isResolved());
        entity.setResolvedAt(domain.getResolvedAt());
        entity.setResolutionNotes(domain.getResolutionNotes());
        return entity;
    }

    private Alert toDomain(AlertEntity entity) {
        return Alert.reconstitute(
            entity.getId(),
            entity.getSystemId(),
            entity.getRuleId(),
            AlertSeverity.valueOf(entity.getSeverity().name()),
            entity.getMessage(),
            entity.getTriggeredAt(),
            entity.isResolved(),
            entity.getResolvedAt(),
            entity.getResolutionNotes()
        );
    }
}
