package com.apm.platform.infrastructure.persistence.repository;

import com.apm.platform.infrastructure.persistence.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AlertJpaRepository extends JpaRepository<AlertEntity, String> {

    List<AlertEntity> findBySystemIdOrderByTriggeredAtDesc(String systemId);

    @Query("SELECT a FROM AlertEntity a WHERE a.systemId = :systemId AND a.resolved = false ORDER BY a.triggeredAt DESC")
    List<AlertEntity> findActiveBySystemId(String systemId);

    List<AlertEntity> findBySeverity(AlertEntity.AlertSeverity severity);

    @Query("SELECT a FROM AlertEntity a WHERE a.systemId = :systemId AND a.triggeredAt BETWEEN :startTime AND :endTime ORDER BY a.triggeredAt DESC")
    List<AlertEntity> findBySystemIdAndTriggeredAtBetween(String systemId, Instant startTime, Instant endTime);

    @Query("SELECT a FROM AlertEntity a WHERE a.resolved = false ORDER BY a.severity DESC, a.triggeredAt DESC")
    List<AlertEntity> findAllActive();

    long countBySystemIdAndResolved(String systemId, boolean resolved);
}
