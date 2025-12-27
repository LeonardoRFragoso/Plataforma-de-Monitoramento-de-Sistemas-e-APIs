package com.apm.platform.infrastructure.persistence.adapter;

import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.valueobject.SystemStatus;
import com.apm.platform.infrastructure.persistence.entity.MonitoredSystemEntity;
import com.apm.platform.infrastructure.persistence.repository.MonitoredSystemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MonitoredSystemRepositoryAdapter implements MonitoredSystemRepository {

    private final MonitoredSystemJpaRepository jpaRepository;

    public MonitoredSystemRepositoryAdapter(MonitoredSystemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MonitoredSystem save(MonitoredSystem system) {
        MonitoredSystemEntity entity = toEntity(system);
        MonitoredSystemEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MonitoredSystem> findById(String systemId) {
        return jpaRepository.findById(systemId).map(this::toDomain);
    }

    @Override
    public Optional<MonitoredSystem> findByName(String name) {
        return jpaRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public List<MonitoredSystem> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MonitoredSystem> findAllActive() {
        return jpaRepository.findAllActiveOrderByName().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MonitoredSystem> findByEnvironment(MonitoredSystem.Environment environment) {
        MonitoredSystemEntity.Environment entityEnvironment = MonitoredSystemEntity.Environment.valueOf(environment.name());
        return jpaRepository.findByEnvironment(entityEnvironment).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public void deleteById(String systemId) {
        jpaRepository.deleteById(systemId);
    }

    private MonitoredSystemEntity toEntity(MonitoredSystem domain) {
        MonitoredSystemEntity entity = new MonitoredSystemEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setBaseUrl(domain.getBaseUrl());
        entity.setType(MonitoredSystemEntity.SystemType.valueOf(domain.getType().name()));
        entity.setEnvironment(MonitoredSystemEntity.Environment.valueOf(domain.getEnvironment().name()));
        entity.setCollectionIntervalSeconds(domain.getCollectionIntervalSeconds());
        entity.setActive(domain.isActive());
        entity.setCurrentStatus(MonitoredSystemEntity.SystemStatus.valueOf(domain.getCurrentStatus().name()));
        entity.setLastCheckAt(domain.getLastCheckAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private MonitoredSystem toDomain(MonitoredSystemEntity entity) {
        return MonitoredSystem.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getBaseUrl(),
            MonitoredSystem.SystemType.valueOf(entity.getType().name()),
            MonitoredSystem.Environment.valueOf(entity.getEnvironment().name()),
            entity.getCollectionIntervalSeconds(),
            entity.isActive(),
            SystemStatus.valueOf(entity.getCurrentStatus().name()),
            entity.getLastCheckAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
