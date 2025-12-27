package com.apm.platform.infrastructure.persistence.repository;

import com.apm.platform.infrastructure.persistence.entity.MetricEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MetricJpaRepository extends JpaRepository<MetricEntity, String> {

    List<MetricEntity> findBySystemIdOrderByCollectedAtDesc(String systemId);

    @Query("SELECT m FROM MetricEntity m WHERE m.systemId = :systemId AND m.collectedAt BETWEEN :startTime AND :endTime ORDER BY m.collectedAt DESC")
    List<MetricEntity> findBySystemIdAndCollectedAtBetween(String systemId, Instant startTime, Instant endTime);

    @Query("SELECT m FROM MetricEntity m WHERE m.systemId = :systemId ORDER BY m.collectedAt DESC")
    List<MetricEntity> findRecentBySystemId(String systemId, Pageable pageable);

    long countBySystemId(String systemId);

    @Modifying
    @Query("DELETE FROM MetricEntity m WHERE m.collectedAt < :timestamp")
    void deleteByCollectedAtBefore(Instant timestamp);
}
