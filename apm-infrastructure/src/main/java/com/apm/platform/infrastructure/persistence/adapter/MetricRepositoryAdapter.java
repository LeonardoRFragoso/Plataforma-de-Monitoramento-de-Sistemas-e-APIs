package com.apm.platform.infrastructure.persistence.adapter;

import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.infrastructure.persistence.entity.MetricEntity;
import com.apm.platform.infrastructure.persistence.repository.MetricJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MetricRepositoryAdapter implements MetricRepository {

    private final MetricJpaRepository jpaRepository;

    public MetricRepositoryAdapter(MetricJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Metric save(Metric metric) {
        MetricEntity entity = toEntity(metric);
        MetricEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Metric> findById(String metricId) {
        return jpaRepository.findById(metricId).map(this::toDomain);
    }

    @Override
    public List<Metric> findBySystemId(String systemId) {
        return jpaRepository.findBySystemIdOrderByCollectedAtDesc(systemId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Metric> findBySystemIdAndTimeRange(String systemId, Instant startTime, Instant endTime) {
        return jpaRepository.findBySystemIdAndCollectedAtBetween(systemId, startTime, endTime).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Metric> findRecentBySystemId(String systemId, int limit) {
        return jpaRepository.findRecentBySystemId(systemId, PageRequest.of(0, limit)).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countBySystemId(String systemId) {
        return jpaRepository.countBySystemId(systemId);
    }

    @Override
    @Transactional
    public void deleteOlderThan(Instant timestamp) {
        jpaRepository.deleteByCollectedAtBefore(timestamp);
    }

    private MetricEntity toEntity(Metric domain) {
        MetricEntity entity = new MetricEntity();
        entity.setId(domain.getId());
        entity.setSystemId(domain.getSystemId());
        entity.setLatencyMs(domain.getLatencyMs());
        entity.setStatusCode(domain.getStatusCode());
        entity.setHasError(domain.hasError());
        entity.setCpuUsagePercent(domain.getCpuUsagePercent());
        entity.setMemoryUsagePercent(domain.getMemoryUsagePercent());
        
        Map<String, String> additionalDataAsString = new HashMap<>();
        domain.getAdditionalData().forEach((key, value) -> 
            additionalDataAsString.put(key, value != null ? value.toString() : null)
        );
        entity.setAdditionalData(additionalDataAsString);
        
        entity.setCollectedAt(domain.getCollectedAt());
        return entity;
    }

    private Metric toDomain(MetricEntity entity) {
        Map<String, Object> additionalData = new HashMap<>(entity.getAdditionalData());
        
        return Metric.reconstitute(
            entity.getId(),
            entity.getSystemId(),
            entity.getLatencyMs(),
            entity.getStatusCode(),
            entity.isHasError(),
            entity.getCpuUsagePercent(),
            entity.getMemoryUsagePercent(),
            additionalData,
            entity.getCollectedAt()
        );
    }
}
