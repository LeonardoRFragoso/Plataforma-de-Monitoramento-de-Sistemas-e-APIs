package com.apm.platform.interfaces.rest.controller;

import com.apm.platform.application.dto.request.QueryMetricsRequest;
import com.apm.platform.application.dto.response.MetricResponse;
import com.apm.platform.application.usecase.QueryHistoricalMetricsUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/systems/{systemId}/metrics")
public class MetricController {

    private final QueryHistoricalMetricsUseCase queryMetricsUseCase;

    public MetricController(QueryHistoricalMetricsUseCase queryMetricsUseCase) {
        this.queryMetricsUseCase = queryMetricsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<MetricResponse>> getMetrics(
            @PathVariable("systemId") String systemId,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        
        Instant start = startTime != null ? startTime : Instant.now().minusSeconds(3600);
        Instant end = endTime != null ? endTime : Instant.now();
        QueryMetricsRequest request = new QueryMetricsRequest(systemId, start, end);
        List<MetricResponse> metrics = queryMetricsUseCase.execute(request);
        
        return ResponseEntity.ok(metrics.stream().limit(limit).toList());
    }

    @GetMapping("/latest")
    public ResponseEntity<MetricResponse> getLatestMetric(@PathVariable("systemId") String systemId) {
        QueryMetricsRequest request = new QueryMetricsRequest(systemId, 
            Instant.now().minusSeconds(3600), Instant.now());
        List<MetricResponse> metrics = queryMetricsUseCase.execute(request);
        
        if (metrics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(metrics.get(0));
    }
}
