package com.apm.platform.interfaces.rest.controller;

import com.apm.platform.application.dto.response.SystemHealthResponse;
import com.apm.platform.application.usecase.EvaluateSystemHealthUseCase;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.valueobject.SystemStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/systems/{systemId}/health")
public class HealthController {

    private final EvaluateSystemHealthUseCase evaluateHealthUseCase;
    private final MonitoredSystemRepository systemRepository;

    public HealthController(EvaluateSystemHealthUseCase evaluateHealthUseCase,
                           MonitoredSystemRepository systemRepository) {
        this.evaluateHealthUseCase = evaluateHealthUseCase;
        this.systemRepository = systemRepository;
    }

    @GetMapping
    public ResponseEntity<SystemHealthResponse> getSystemHealth(@PathVariable String systemId) {
        MonitoredSystem system = systemRepository.findById(systemId)
            .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));
        
        SystemStatus status = evaluateHealthUseCase.execute(systemId);
        
        SystemHealthResponse response = new SystemHealthResponse(
            systemId,
            system.getName(),
            status.name(),
            status.getDescription(),
            status == SystemStatus.UP,
            status != SystemStatus.DOWN
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<SystemHealthResponse> evaluateHealth(@PathVariable String systemId) {
        MonitoredSystem system = systemRepository.findById(systemId)
            .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));
        
        SystemStatus status = evaluateHealthUseCase.execute(systemId);
        
        SystemHealthResponse response = new SystemHealthResponse(
            systemId,
            system.getName(),
            status.name(),
            status.getDescription(),
            status == SystemStatus.UP,
            status != SystemStatus.DOWN
        );
        
        return ResponseEntity.ok(response);
    }
}
