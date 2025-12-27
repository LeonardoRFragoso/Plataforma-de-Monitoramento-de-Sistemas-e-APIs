package com.apm.platform.infrastructure.persistence.adapter;

import com.apm.platform.domain.entity.Incident;
import com.apm.platform.domain.port.outgoing.IncidentRepository;
import com.apm.platform.domain.valueobject.SystemStatus;
import com.apm.platform.infrastructure.persistence.entity.IncidentEntity;
import com.apm.platform.infrastructure.persistence.repository.IncidentJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class IncidentRepositoryAdapter implements IncidentRepository {

    private final IncidentJpaRepository jpaRepository;

    public IncidentRepositoryAdapter(IncidentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Incident save(Incident incident) {
        IncidentEntity entity = toEntity(incident);
        IncidentEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Incident> findById(String incidentId) {
        return jpaRepository.findById(incidentId).map(this::toDomain);
    }

    @Override
    public List<Incident> findBySystemId(String systemId) {
        return jpaRepository.findBySystemIdOrderByStartedAtDesc(systemId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Incident> findActiveBySystemId(String systemId) {
        return jpaRepository.findActiveBySystemId(systemId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Incident> findBySystemIdAndTimeRange(String systemId, Instant startTime, Instant endTime) {
        return jpaRepository.findBySystemIdAndStartedAtBetween(systemId, startTime, endTime).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Incident> findActiveIncidentBySystemId(String systemId) {
        return jpaRepository.findActiveIncidentBySystemId(systemId).map(this::toDomain);
    }

    @Override
    public long countActiveBySystemId(String systemId) {
        return jpaRepository.countBySystemIdAndResolved(systemId, false);
    }

    private IncidentEntity toEntity(Incident domain) {
        IncidentEntity entity = new IncidentEntity();
        entity.setId(domain.getId());
        entity.setSystemId(domain.getSystemId());
        entity.setDetectedStatus(IncidentEntity.SystemStatus.valueOf(domain.getDetectedStatus().name()));
        entity.setDescription(domain.getDescription());
        entity.setStartedAt(domain.getStartedAt());
        entity.setResolved(domain.isResolved());
        entity.setResolvedAt(domain.getResolvedAt());
        entity.setDowntime(domain.getDowntime());
        entity.setRootCause(domain.getRootCause());
        return entity;
    }

    private Incident toDomain(IncidentEntity entity) {
        return Incident.reconstitute(
            entity.getId(),
            entity.getSystemId(),
            SystemStatus.valueOf(entity.getDetectedStatus().name()),
            entity.getDescription(),
            entity.getStartedAt(),
            entity.isResolved(),
            entity.getResolvedAt(),
            entity.getDowntime(),
            entity.getRootCause()
        );
    }
}
