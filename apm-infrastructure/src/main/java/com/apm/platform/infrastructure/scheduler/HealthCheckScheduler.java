package com.apm.platform.infrastructure.scheduler;

import com.apm.platform.application.usecase.EvaluateSystemHealthUseCase;
import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.valueobject.SystemStatus;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HealthCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckScheduler.class);

    private final MonitoredSystemRepository systemRepository;
    private final EvaluateSystemHealthUseCase evaluateHealthUseCase;

    public HealthCheckScheduler(MonitoredSystemRepository systemRepository,
                               EvaluateSystemHealthUseCase evaluateHealthUseCase) {
        this.systemRepository = systemRepository;
        this.evaluateHealthUseCase = evaluateHealthUseCase;
    }

    @Scheduled(fixedDelayString = "${apm.scheduler.health-check.interval-ms:60000}")
    @SchedulerLock(name = "healthCheckScheduler", lockAtMostFor = "5m", lockAtLeastFor = "10s")
    public void evaluateHealth() {
        logger.info("Starting health evaluation cycle");

        List<MonitoredSystem> activeSystems = systemRepository.findAllActive();

        int healthyCount = 0;
        int degradedCount = 0;
        int downCount = 0;

        for (MonitoredSystem system : activeSystems) {
            try {
                SystemStatus status = evaluateHealthUseCase.execute(system.getId());
                
                switch (status) {
                    case UP -> healthyCount++;
                    case DEGRADED -> degradedCount++;
                    case DOWN -> downCount++;
                }

                logger.debug("Health evaluated for system: {} - Status: {}", system.getName(), status);
            } catch (Exception e) {
                logger.error("Failed to evaluate health for system: {}", system.getName(), e);
            }
        }

        logger.info("Health evaluation completed. Healthy: {}, Degraded: {}, Down: {}", 
            healthyCount, degradedCount, downCount);
    }
}
