package com.apm.platform.infrastructure.persistence.repository;

import com.apm.platform.infrastructure.persistence.entity.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidentJpaRepository extends JpaRepository<IncidentEntity, String> {

    List<IncidentEntity> findBySystemIdOrderByStartedAtDesc(String systemId);

    @Query("SELECT i FROM IncidentEntity i WHERE i.systemId = :systemId AND i.resolved = false ORDER BY i.startedAt DESC")
    List<IncidentEntity> findActiveBySystemId(@Param("systemId") String systemId);

    @Query("SELECT i FROM IncidentEntity i WHERE i.systemId = :systemId AND i.startedAt BETWEEN :startTime AND :endTime ORDER BY i.startedAt DESC")
    List<IncidentEntity> findBySystemIdAndStartedAtBetween(@Param("systemId") String systemId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    @Query("SELECT i FROM IncidentEntity i WHERE i.systemId = :systemId AND i.resolved = false ORDER BY i.startedAt DESC LIMIT 1")
    Optional<IncidentEntity> findActiveIncidentBySystemId(@Param("systemId") String systemId);

    long countBySystemIdAndResolved(String systemId, boolean resolved);
}
