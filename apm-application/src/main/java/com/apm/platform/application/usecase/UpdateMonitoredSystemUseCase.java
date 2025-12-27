package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.request.UpdateMonitoredSystemRequest;
import com.apm.platform.application.dto.response.MonitoredSystemResponse;
import com.apm.platform.application.mapper.MonitoredSystemMapper;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.incoming.UpdateMonitoredSystem;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;

import java.util.List;

public class UpdateMonitoredSystemUseCase implements UpdateMonitoredSystem {

    private final MonitoredSystemRepository systemRepository;

    public UpdateMonitoredSystemUseCase(MonitoredSystemRepository systemRepository) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        this.systemRepository = systemRepository;
    }

    @Override
    public MonitoredSystem execute(String systemId, String name, String baseUrl,
                                   MonitoredSystem.SystemType type, MonitoredSystem.Environment environment,
                                   int collectionIntervalSeconds) {
        if (systemId == null || systemId.isBlank()) {
            throw new IllegalArgumentException("System ID cannot be null or blank");
        }

        MonitoredSystem system = systemRepository.findById(systemId)
                .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));

        system.updateDetails(name, baseUrl, type, environment, collectionIntervalSeconds);

        return systemRepository.save(system);
    }

    public MonitoredSystemResponse execute(UpdateMonitoredSystemRequest request) {
        List<String> validationErrors = request.validate();
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", validationErrors));
        }

        MonitoredSystem.SystemType type = MonitoredSystemMapper.parseSystemType(request.getType());
        MonitoredSystem.Environment environment = MonitoredSystemMapper.parseEnvironment(request.getEnvironment());

        MonitoredSystem system = execute(
            request.getSystemId(),
            request.getName(),
            request.getBaseUrl(),
            type,
            environment,
            request.getCollectionIntervalSeconds()
        );

        return MonitoredSystemMapper.toResponse(system);
    }
}
