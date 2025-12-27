package com.apm.platform.infrastructure.scheduler;

import com.apm.platform.domain.entity.AlertRule;
import com.apm.platform.domain.entity.Metric;
import com.apm.platform.domain.port.outgoing.AlertRuleRepository;
import com.apm.platform.domain.port.outgoing.MetricRepository;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.service.AlertRuleDomainService;
import com.apm.platform.application.usecase.TriggerAlertUseCase;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlertEvaluationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AlertEvaluationScheduler.class);
    private static final int RECENT_METRICS_LIMIT = 10;

    private final MonitoredSystemRepository systemRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final MetricRepository metricRepository;
    private final AlertRuleDomainService alertRuleService;
    private final TriggerAlertUseCase triggerAlertUseCase;

    public AlertEvaluationScheduler(MonitoredSystemRepository systemRepository,
                                   AlertRuleRepository alertRuleRepository,
                                   MetricRepository metricRepository,
                                   AlertRuleDomainService alertRuleService,
                                   TriggerAlertUseCase triggerAlertUseCase) {
        this.systemRepository = systemRepository;
        this.alertRuleRepository = alertRuleRepository;
        this.metricRepository = metricRepository;
        this.alertRuleService = alertRuleService;
        this.triggerAlertUseCase = triggerAlertUseCase;
    }

    @Scheduled(fixedDelayString = "${apm.scheduler.alert-evaluation.interval-ms:60000}")
    @SchedulerLock(name = "alertEvaluationScheduler", lockAtMostFor = "5m", lockAtLeastFor = "10s")
    public void evaluateAlerts() {
        logger.info("Starting alert evaluation cycle");

        List<AlertRule> allRules = alertRuleRepository.findAll();
        int triggeredCount = 0;

        for (AlertRule rule : allRules) {
            if (!rule.isEnabled()) {
                continue;
            }

            try {
                List<Metric> recentMetrics = metricRepository.findRecentBySystemId(
                    rule.getSystemId(), RECENT_METRICS_LIMIT
                );

                if (alertRuleService.shouldTriggerAlert(rule, recentMetrics)) {
                    String message = String.format("Alert rule '%s' violated for system. " +
                        "Threshold: %.2f, Type: %s", 
                        rule.getName(), rule.getThresholdValue(), rule.getType());
                    
                    triggerAlertUseCase.execute(rule.getSystemId(), rule.getId(), message);
                    triggeredCount++;
                    
                    logger.warn("Alert triggered: {}", message);
                }
            } catch (Exception e) {
                logger.error("Failed to evaluate alert rule: {}", rule.getName(), e);
            }
        }

        logger.info("Alert evaluation completed. Alerts triggered: {}", triggeredCount);
    }
}
