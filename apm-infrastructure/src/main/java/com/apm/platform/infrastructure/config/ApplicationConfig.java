package com.apm.platform.infrastructure.config;

import com.apm.platform.application.usecase.*;
import com.apm.platform.domain.port.outgoing.*;
import com.apm.platform.domain.service.AlertRuleDomainService;
import com.apm.platform.domain.service.HealthEvaluationDomainService;
import com.apm.platform.domain.service.UptimeCalculationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public HealthEvaluationDomainService healthEvaluationDomainService() {
        return new HealthEvaluationDomainService();
    }

    @Bean
    public UptimeCalculationDomainService uptimeCalculationDomainService() {
        return new UptimeCalculationDomainService();
    }

    @Bean
    public AlertRuleDomainService alertRuleDomainService() {
        return new AlertRuleDomainService();
    }

    @Bean
    public RegisterMonitoredSystemUseCase registerMonitoredSystemUseCase(
            MonitoredSystemRepository systemRepository) {
        return new RegisterMonitoredSystemUseCase(systemRepository);
    }

    @Bean
    public UpdateMonitoredSystemUseCase updateMonitoredSystemUseCase(
            MonitoredSystemRepository systemRepository) {
        return new UpdateMonitoredSystemUseCase(systemRepository);
    }

    @Bean
    public ActivateMonitoredSystemUseCase activateMonitoredSystemUseCase(
            MonitoredSystemRepository systemRepository) {
        return new ActivateMonitoredSystemUseCase(systemRepository);
    }

    @Bean
    public DeactivateMonitoredSystemUseCase deactivateMonitoredSystemUseCase(
            MonitoredSystemRepository systemRepository) {
        return new DeactivateMonitoredSystemUseCase(systemRepository);
    }

    @Bean
    public CollectSystemMetricsUseCase collectSystemMetricsUseCase(
            MonitoredSystemRepository systemRepository,
            MetricRepository metricRepository,
            MetricCollectorGateway collectorGateway,
            DomainEventPublisher eventPublisher) {
        return new CollectSystemMetricsUseCase(
            systemRepository, metricRepository, collectorGateway, eventPublisher);
    }

    @Bean
    public EvaluateSystemHealthUseCase evaluateSystemHealthUseCase(
            MonitoredSystemRepository systemRepository,
            MetricRepository metricRepository,
            HealthEvaluationDomainService healthService,
            DomainEventPublisher eventPublisher) {
        return new EvaluateSystemHealthUseCase(
            systemRepository, metricRepository, healthService, eventPublisher);
    }

    @Bean
    public TriggerAlertUseCase triggerAlertUseCase(
            MonitoredSystemRepository systemRepository,
            AlertRuleRepository alertRuleRepository,
            AlertRepository alertRepository,
            AlertNotifierGateway notifierGateway,
            DomainEventPublisher eventPublisher) {
        return new TriggerAlertUseCase(
            systemRepository, alertRuleRepository, alertRepository, notifierGateway, eventPublisher);
    }

    @Bean
    public QueryHistoricalMetricsUseCase queryHistoricalMetricsUseCase(
            MonitoredSystemRepository systemRepository,
            MetricRepository metricRepository) {
        return new QueryHistoricalMetricsUseCase(systemRepository, metricRepository);
    }

    @Bean
    public GenerateUptimeReportUseCase generateUptimeReportUseCase(
            MonitoredSystemRepository systemRepository,
            MetricRepository metricRepository,
            UptimeCalculationDomainService uptimeService) {
        return new GenerateUptimeReportUseCase(systemRepository, metricRepository, uptimeService);
    }

    @Bean
    public CreateAlertRuleUseCase createAlertRuleUseCase(
            MonitoredSystemRepository systemRepository,
            AlertRuleRepository alertRuleRepository) {
        return new CreateAlertRuleUseCase(systemRepository, alertRuleRepository);
    }

    @Bean
    public CalculateSystemUptimeUseCase calculateSystemUptimeUseCase(
            MonitoredSystemRepository systemRepository,
            MetricRepository metricRepository,
            UptimeCalculationDomainService uptimeService) {
        return new CalculateSystemUptimeUseCase(systemRepository, metricRepository, uptimeService);
    }
}
