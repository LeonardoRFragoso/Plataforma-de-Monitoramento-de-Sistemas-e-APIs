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
            @PathVariable("systemId") String systemId,
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
    public ResponseEntity<List<AlertRuleResponse>> getAlertRules(@PathVariable("systemId") String systemId) {
        List<AlertRuleResponse> rules = alertRuleRepository.findBySystemId(systemId)
            .stream()
            .map(AlertRuleMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/systems/{systemId}/alerts")
    public ResponseEntity<List<AlertResponse>> getAlerts(
            @PathVariable("systemId") String systemId,
            @RequestParam(value = "resolved", required = false) Boolean resolved) {
        
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

    @GetMapping("/alert-rules")
    public ResponseEntity<List<AlertRuleResponse>> getAllAlertRules() {
        List<AlertRuleResponse> rules = alertRuleRepository.findAll()
            .stream()
            .map(AlertRuleMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(rules);
    }

    @DeleteMapping("/alert-rules/{ruleId}")
    public ResponseEntity<Void> deleteAlertRule(@PathVariable("ruleId") String ruleId) {
        alertRuleRepository.deleteById(ruleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/alert-rules/{ruleId}/enable")
    public ResponseEntity<Void> enableAlertRule(@PathVariable("ruleId") String ruleId) {
        alertRuleRepository.findById(ruleId).ifPresent(rule -> {
            rule.enable();
            alertRuleRepository.save(rule);
        });
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/alert-rules/{ruleId}/disable")
    public ResponseEntity<Void> disableAlertRule(@PathVariable("ruleId") String ruleId) {
        alertRuleRepository.findById(ruleId).ifPresent(rule -> {
            rule.disable();
            alertRuleRepository.save(rule);
        });
        return ResponseEntity.noContent().build();
    }
}
