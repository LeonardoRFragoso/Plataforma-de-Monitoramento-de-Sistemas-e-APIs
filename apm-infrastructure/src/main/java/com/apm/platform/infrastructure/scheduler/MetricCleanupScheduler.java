package com.apm.platform.infrastructure.scheduler;

import com.apm.platform.domain.port.outgoing.MetricRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class MetricCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MetricCleanupScheduler.class);

    private final MetricRepository metricRepository;

    @Value("${apm.scheduler.metric-cleanup.retention-days:30}")
    private int retentionDays;

    public MetricCleanupScheduler(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @Scheduled(cron = "${apm.scheduler.metric-cleanup.cron:0 0 2 * * ?}")
    @SchedulerLock(name = "metricCleanupScheduler", lockAtMostFor = "10m", lockAtLeastFor = "1m")
    public void cleanupOldMetrics() {
        logger.info("Starting metric cleanup. Retention: {} days", retentionDays);

        try {
            Instant threshold = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
            metricRepository.deleteOlderThan(threshold);
            
            logger.info("Metric cleanup completed successfully. Deleted metrics older than {}", threshold);
        } catch (Exception e) {
            logger.error("Failed to cleanup old metrics", e);
        }
    }
}
