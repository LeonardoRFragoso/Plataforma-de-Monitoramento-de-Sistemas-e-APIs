package com.apm.platform.application.usecase;

import com.apm.platform.application.dto.request.RegisterMonitoredSystemRequest;
import com.apm.platform.application.dto.response.MonitoredSystemResponse;
import com.apm.platform.application.mapper.MonitoredSystemMapper;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.DuplicateSystemException;
import com.apm.platform.domain.port.incoming.RegisterMonitoredSystem;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;

import java.util.List;

public class RegisterMonitoredSystemUseCase implements RegisterMonitoredSystem {

    private final MonitoredSystemRepository systemRepository;

    public RegisterMonitoredSystemUseCase(MonitoredSystemRepository systemRepository) {
        if (systemRepository == null) {
            throw new IllegalArgumentException("MonitoredSystemRepository cannot be null");
        }
        this.systemRepository = systemRepository;
    }

    @Override
    public MonitoredSystem execute(String name, String baseUrl, MonitoredSystem.SystemType type,
                                   MonitoredSystem.Environment environment, int collectionIntervalSeconds) {
        
        if (systemRepository.existsByName(name)) {
            throw new DuplicateSystemException(name);
        }

        MonitoredSystem system = MonitoredSystem.create(
            name, 
            baseUrl, 
            type, 
            environment, 
            collectionIntervalSeconds
        );

        return systemRepository.save(system);
    }

    public MonitoredSystemResponse execute(RegisterMonitoredSystemRequest request) {
        List<String> validationErrors = request.validate();
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", validationErrors));
        }

        MonitoredSystem.SystemType type = MonitoredSystemMapper.parseSystemType(request.getType());
        MonitoredSystem.Environment environment = MonitoredSystemMapper.parseEnvironment(request.getEnvironment());

        MonitoredSystem system = execute(
            request.getName(),
            request.getBaseUrl(),
            type,
            environment,
            request.getCollectionIntervalSeconds()
        );

        return MonitoredSystemMapper.toResponse(system);
    }
}
