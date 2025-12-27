package com.apm.platform.domain.port.outgoing;

import com.apm.platform.domain.entity.Incident;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository {
    Incident save(Incident incident);
    Optional<Incident> findById(String incidentId);
    List<Incident> findBySystemId(String systemId);
    List<Incident> findActiveBySystemId(String systemId);
    List<Incident> findBySystemIdAndTimeRange(String systemId, Instant startTime, Instant endTime);
    Optional<Incident> findActiveIncidentBySystemId(String systemId);
    long countActiveBySystemId(String systemId);
}
