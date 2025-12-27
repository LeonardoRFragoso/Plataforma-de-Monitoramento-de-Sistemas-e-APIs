package com.apm.platform.interfaces.rest.controller;

import com.apm.platform.application.dto.request.RegisterMonitoredSystemRequest;
import com.apm.platform.application.dto.request.UpdateMonitoredSystemRequest;
import com.apm.platform.application.dto.response.MonitoredSystemResponse;
import com.apm.platform.application.usecase.*;
import com.apm.platform.application.mapper.MonitoredSystemMapper;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/systems")
public class MonitoredSystemController {

    private static final Logger logger = LoggerFactory.getLogger(MonitoredSystemController.class);

    private final RegisterMonitoredSystemUseCase registerSystemUseCase;
    private final UpdateMonitoredSystemUseCase updateSystemUseCase;
    private final ActivateMonitoredSystemUseCase activateSystemUseCase;
    private final DeactivateMonitoredSystemUseCase deactivateSystemUseCase;
    private final MonitoredSystemRepository systemRepository;

    public MonitoredSystemController(
            RegisterMonitoredSystemUseCase registerSystemUseCase,
            UpdateMonitoredSystemUseCase updateSystemUseCase,
            ActivateMonitoredSystemUseCase activateSystemUseCase,
            DeactivateMonitoredSystemUseCase deactivateSystemUseCase,
            MonitoredSystemRepository systemRepository) {
        this.registerSystemUseCase = registerSystemUseCase;
        this.updateSystemUseCase = updateSystemUseCase;
        this.activateSystemUseCase = activateSystemUseCase;
        this.deactivateSystemUseCase = deactivateSystemUseCase;
        this.systemRepository = systemRepository;
    }

    @PostMapping
    public ResponseEntity<MonitoredSystemResponse> registerSystem(
            @RequestBody RegisterMonitoredSystemRequest request) {
        
        MonitoredSystemResponse response = registerSystemUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{systemId}")
    public ResponseEntity<MonitoredSystemResponse> getSystem(
            @PathVariable("systemId") String systemId) {
        
        MonitoredSystem system = systemRepository.findById(systemId)
            .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));
        
        return ResponseEntity.ok(MonitoredSystemMapper.toResponse(system));
    }

    @GetMapping
    public ResponseEntity<List<MonitoredSystemResponse>> listSystems(
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "environment", required = false) String environment) {
        
        logger.info("GET /api/v1/systems - active: {}, environment: {}", active, environment);
        
        try {
            List<MonitoredSystem> systems = Boolean.TRUE.equals(active) 
                ? systemRepository.findAllActive() 
                : systemRepository.findAll();
            
            logger.info("Found {} systems", systems.size());
            
            List<MonitoredSystemResponse> responses = systems.stream()
                .map(MonitoredSystemMapper::toResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error listing systems", e);
            throw e;
        }
    }

    @PutMapping("/{systemId}")
    public ResponseEntity<MonitoredSystemResponse> updateSystem(
            @PathVariable("systemId") String systemId,
            @RequestBody UpdateMonitoredSystemRequest request) {
        
        UpdateMonitoredSystemRequest requestWithId = new UpdateMonitoredSystemRequest(
            systemId,
            request.getName(),
            request.getBaseUrl(),
            request.getType(),
            request.getEnvironment(),
            request.getCollectionIntervalSeconds()
        );
        
        MonitoredSystemResponse response = updateSystemUseCase.execute(requestWithId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{systemId}/activate")
    public ResponseEntity<Void> activateSystem(@PathVariable("systemId") String systemId) {
        activateSystemUseCase.execute(systemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{systemId}/deactivate")
    public ResponseEntity<Void> deactivateSystem(@PathVariable("systemId") String systemId) {
        deactivateSystemUseCase.execute(systemId);
        return ResponseEntity.noContent().build();
    }
}
