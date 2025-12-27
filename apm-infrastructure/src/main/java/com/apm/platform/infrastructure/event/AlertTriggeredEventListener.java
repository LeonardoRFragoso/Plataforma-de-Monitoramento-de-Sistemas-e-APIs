package com.apm.platform.infrastructure.event;

import com.apm.platform.domain.event.AlertTriggeredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AlertTriggeredEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AlertTriggeredEventListener.class);

    @EventListener
    @Async
    public void handleAlertTriggered(AlertTriggeredEvent event) {
        logger.info("Handling AlertTriggeredEvent: Alert {} triggered for system {} with severity {}", 
            event.getAlertId(), event.getSystemId(), event.getSeverity());

        if (event.isCritical()) {
            logger.error("⚠️ CRITICAL ALERT: {} - {}", event.getSystemId(), event.getMessage());
        }
    }
}
