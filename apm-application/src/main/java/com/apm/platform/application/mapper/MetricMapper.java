package com.apm.platform.application.mapper;

import com.apm.platform.application.dto.response.MetricResponse;
import com.apm.platform.domain.entity.Metric;

import java.util.List;
import java.util.stream.Collectors;

public class MetricMapper {

    public static MetricResponse toResponse(Metric metric) {
        if (metric == null) {
            return null;
        }

        return new MetricResponse(
            metric.getId(),
            metric.getSystemId(),
            metric.getLatencyMs(),
            metric.getStatusCode(),
            metric.hasError(),
            metric.getCpuUsagePercent(),
            metric.getMemoryUsagePercent(),
            metric.getAdditionalData(),
            metric.getCollectedAt()
        );
    }

    public static List<MetricResponse> toResponseList(List<Metric> metrics) {
        if (metrics == null) {
            return List.of();
        }

        return metrics.stream()
                .map(MetricMapper::toResponse)
                .collect(Collectors.toList());
    }
}
