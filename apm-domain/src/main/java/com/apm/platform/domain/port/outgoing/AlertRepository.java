package com.apm.platform.domain.port.outgoing;

import com.apm.platform.domain.entity.Alert;
import com.apm.platform.domain.valueobject.AlertSeverity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AlertRepository {
    Alert save(Alert alert);
    Optional<Alert> findById(String alertId);
    List<Alert> findBySystemId(String systemId);
    List<Alert> findActiveBySystemId(String systemId);
    List<Alert> findBySeverity(AlertSeverity severity);
    List<Alert> findBySystemIdAndTimeRange(String systemId, Instant startTime, Instant endTime);
    List<Alert> findAllActive();
    long countActiveBySystemId(String systemId);
}
