package com.apm.platform.infrastructure.scheduler;

import com.apm.platform.application.usecase.CollectSystemMetricsUseCase;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetricCollectionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MetricCollectionScheduler.class);

    private final MonitoredSystemRepository systemRepository;
    private final CollectSystemMetricsUseCase collectMetricsUseCase;

    public MetricCollectionScheduler(MonitoredSystemRepository systemRepository,
                                    CollectSystemMetricsUseCase collectMetricsUseCase) {
        this.systemRepository = systemRepository;
        this.collectMetricsUseCase = collectMetricsUseCase;
    }

    @Scheduled(fixedDelayString = "${apm.scheduler.metric-collection.interval-ms:30000}")
    @SchedulerLock(name = "metricCollectionScheduler", lockAtMostFor = "5m", lockAtLeastFor = "10s")
    public void collectMetrics() {
        logger.info("Starting metric collection cycle");

        List<MonitoredSystem> activeSystems = systemRepository.findAllActive();
        
        int successCount = 0;
        int failureCount = 0;

        for (MonitoredSystem system : activeSystems) {
            try {
                collectMetricsUseCase.execute(system.getId());
                successCount++;
                logger.debug("Collected metrics for system: {}", system.getName());
            } catch (Exception e) {
                failureCount++;
                logger.error("Failed to collect metrics for system: {}", system.getName(), e);
            }
        }

        logger.info("Metric collection cycle completed. Success: {}, Failures: {}", successCount, failureCount);
    }
}
