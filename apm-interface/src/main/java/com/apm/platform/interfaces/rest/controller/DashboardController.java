package com.apm.platform.interfaces.rest.controller;

import com.apm.platform.application.dto.response.MonitoredSystemResponse;
import com.apm.platform.application.dto.response.AlertResponse;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.port.outgoing.AlertRepository;
import com.apm.platform.application.mapper.MonitoredSystemMapper;
import com.apm.platform.application.mapper.AlertMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final MonitoredSystemRepository systemRepository;
    private final AlertRepository alertRepository;

    public DashboardController(
            MonitoredSystemRepository systemRepository,
            AlertRepository alertRepository) {
        this.systemRepository = systemRepository;
        this.alertRepository = alertRepository;
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        List<MonitoredSystemResponse> systems = systemRepository.findAll()
            .stream()
            .map(MonitoredSystemMapper::toResponse)
            .collect(Collectors.toList());

        List<AlertResponse> activeAlerts = alertRepository.findAllActive()
            .stream()
            .map(AlertMapper::toResponse)
            .collect(Collectors.toList());

        long totalSystems = systems.size();
        long activeSystems = systems.stream().filter(MonitoredSystemResponse::isActive).count();
        long healthySystems = systems.stream()
            .filter(s -> "UP".equals(s.getCurrentStatus()))
            .count();
        long degradedSystems = systems.stream()
            .filter(s -> "DEGRADED".equals(s.getCurrentStatus()))
            .count();
        long downSystems = systems.stream()
            .filter(s -> "DOWN".equals(s.getCurrentStatus()))
            .count();

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalSystems", totalSystems);
        overview.put("activeSystems", activeSystems);
        overview.put("healthySystems", healthySystems);
        overview.put("degradedSystems", degradedSystems);
        overview.put("downSystems", downSystems);
        overview.put("activeAlerts", activeAlerts.size());
        overview.put("criticalAlerts", activeAlerts.stream()
            .filter(a -> "CRITICAL".equals(a.getSeverity()))
            .count());

        return ResponseEntity.ok(overview);
    }

    @GetMapping("/systems")
    public ResponseEntity<List<MonitoredSystemResponse>> getDashboardSystems() {
        List<MonitoredSystemResponse> systems = systemRepository.findAllActive()
            .stream()
            .map(MonitoredSystemMapper::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(systems);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertResponse>> getDashboardAlerts(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<AlertResponse> alerts = alertRepository.findAllActive()
            .stream()
            .limit(limit)
            .map(AlertMapper::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(alerts);
    }
}
