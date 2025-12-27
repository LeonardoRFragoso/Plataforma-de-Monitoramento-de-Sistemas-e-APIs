package com.apm.platform.infrastructure.persistence.repository;

import com.apm.platform.infrastructure.persistence.entity.MonitoredSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoredSystemJpaRepository extends JpaRepository<MonitoredSystemEntity, String> {

    Optional<MonitoredSystemEntity> findByName(String name);

    boolean existsByName(String name);

    List<MonitoredSystemEntity> findByActive(boolean active);

    @Query("SELECT s FROM MonitoredSystemEntity s WHERE s.environment = :environment")
    List<MonitoredSystemEntity> findByEnvironment(MonitoredSystemEntity.Environment environment);

    @Query("SELECT s FROM MonitoredSystemEntity s WHERE s.active = true ORDER BY s.name")
    List<MonitoredSystemEntity> findAllActiveOrderByName();
}
