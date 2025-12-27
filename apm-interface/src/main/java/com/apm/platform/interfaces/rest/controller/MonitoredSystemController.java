package com.apm.platform.interfaces.rest.controller;

import com.apm.platform.application.dto.request.RegisterMonitoredSystemRequest;
import com.apm.platform.application.dto.request.UpdateMonitoredSystemRequest;
import com.apm.platform.application.dto.response.MonitoredSystemResponse;
import com.apm.platform.application.usecase.*;
import com.apm.platform.application.mapper.MonitoredSystemMapper;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/systems")
public class MonitoredSystemController {

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
            @PathVariable String systemId) {
        
        MonitoredSystem system = systemRepository.findById(systemId)
            .orElseThrow(() -> new MonitoredSystemNotFoundException(systemId));
        
        return ResponseEntity.ok(MonitoredSystemMapper.toResponse(system));
    }

    @GetMapping
    public ResponseEntity<List<MonitoredSystemResponse>> listSystems(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String environment) {
        
        List<MonitoredSystem> systems = Boolean.TRUE.equals(active) 
            ? systemRepository.findAllActive() 
            : systemRepository.findAll();
        
        List<MonitoredSystemResponse> responses = systems.stream()
            .map(MonitoredSystemMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{systemId}")
    public ResponseEntity<MonitoredSystemResponse> updateSystem(
            @PathVariable String systemId,
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
    public ResponseEntity<Void> activateSystem(@PathVariable String systemId) {
        activateSystemUseCase.execute(systemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{systemId}/deactivate")
    public ResponseEntity<Void> deactivateSystem(@PathVariable String systemId) {
        deactivateSystemUseCase.execute(systemId);
        return ResponseEntity.noContent().build();
    }
}
