package com.apm.platform.domain.port.outgoing;

import com.apm.platform.domain.entity.MonitoredSystem;

import java.util.List;
import java.util.Optional;

public interface MonitoredSystemRepository {
    MonitoredSystem save(MonitoredSystem system);
    Optional<MonitoredSystem> findById(String systemId);
    Optional<MonitoredSystem> findByName(String name);
    List<MonitoredSystem> findAll();
    List<MonitoredSystem> findAllActive();
    List<MonitoredSystem> findByEnvironment(MonitoredSystem.Environment environment);
    boolean existsByName(String name);
    void deleteById(String systemId);
}
