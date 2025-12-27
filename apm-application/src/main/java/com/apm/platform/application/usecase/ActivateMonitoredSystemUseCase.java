package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.response.MonitoredSystemResponse;
import com.apm.platform.application.mapper.MonitoredSystemMapper;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;

public class ActivateMonitoredSystemUseCase {

    private final MonitoredSystemRepository systemRepository;

    public ActivateMonitoredSystemUseCase(MonitoredSystemRepository systemRepository) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        this.systemRepository = systemRepository;
    }

    public MonitoredSystem execute(String systemId) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }

        MonitoredSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        system.activate();

        return systemRepository.save(system);
    }

    public MonitoredSystemResponse executeAndReturnResponse(String systemId) {
        MonitoredSystem system = execute(systemId);
        return MonitoredSystemMapper.toResponse(system);
    }
}
