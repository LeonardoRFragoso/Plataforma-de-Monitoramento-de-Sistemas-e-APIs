package com.apm.platform.infrastructure.event;

import com.apm.platform.domain.event.IncidentCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class IncidentCreatedEventListener {

    private static final Logger logger = LoggerFactory.getLogger(IncidentCreatedEventListener.class);

    @EventListener
    @Async
    public void handleIncidentCreated(IncidentCreatedEvent event) {
        logger.error("Handling IncidentCreatedEvent: Incident {} created for system {} with status {} - {}", 
            event.getIncidentId(), 
            event.getSystemId(),
            event.getDetectedStatus(),
            event.getDescription());

        if (event.isCritical()) {
            logger.error("🚨 CRITICAL INCIDENT: System {} is experiencing a critical incident", event.getSystemId());
        }
    }
}
