package com.apm.platform.interfaces.rest.controller;

import com.apm.platform.application.dto.request.CreateAlertRuleRequest;
import com.apm.platform.application.dto.response.AlertResponse;
import com.apm.platform.application.dto.response.AlertRuleResponse;
import com.apm.platform.application.usecase.CreateAlertRuleUseCase;
import com.apm.platform.domain.port.outgoing.AlertRepository;
import com.apm.platform.domain.port.outgoing.AlertRuleRepository;
import com.apm.platform.application.mapper.AlertMapper;
import com.apm.platform.application.mapper.AlertRuleMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class AlertController {

    private final CreateAlertRuleUseCase createAlertRuleUseCase;
    private final AlertRuleRepository alertRuleRepository;
    private final AlertRepository alertRepository;

    public AlertController(
            CreateAlertRuleUseCase createAlertRuleUseCase,
            AlertRuleRepository alertRuleRepository,
            AlertRepository alertRepository) {
        this.createAlertRuleUseCase = createAlertRuleUseCase;
        this.alertRuleRepository = alertRuleRepository;
        this.alertRepository = alertRepository;
    }

    @PostMapping("/systems/{systemId}/alert-rules")
    public ResponseEntity<AlertRuleResponse> createAlertRule(
            @PathVariable String systemId,
            @RequestBody CreateAlertRuleRequest request) {
        
        CreateAlertRuleRequest requestWithId = new CreateAlertRuleRequest(
            systemId,
            request.getName(),
            request.getType(),
            request.getSeverity(),
            request.getThresholdValue(),
            request.getConsecutiveViolations()
        );
        
        AlertRuleResponse response = createAlertRuleUseCase.execute(requestWithId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/systems/{systemId}/alert-rules")
    public ResponseEntity<List<AlertRuleResponse>> getAlertRules(@PathVariable String systemId) {
        List<AlertRuleResponse> rules = alertRuleRepository.findBySystemId(systemId)
            .stream()
            .map(AlertRuleMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/systems/{systemId}/alerts")
    public ResponseEntity<List<AlertResponse>> getAlerts(
            @PathVariable String systemId,
            @RequestParam(required = false) Boolean resolved) {
        
        List<AlertResponse> alerts = resolved != null && !resolved
            ? alertRepository.findActiveBySystemId(systemId)
                .stream()
                .map(AlertMapper::toResponse)
                .collect(Collectors.toList())
            : alertRepository.findBySystemId(systemId)
                .stream()
                .map(AlertMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/alerts/active")
    public ResponseEntity<List<AlertResponse>> getAllActiveAlerts() {
        List<AlertResponse> alerts = alertRepository.findAllActive()
            .stream()
            .map(AlertMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(alerts);
    }
}
