package com.apm.platform.infrastructure.event;

import com.apm.platform.domain.event.SystemHealthDegradedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SystemHealthDegradedEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SystemHealthDegradedEventListener.class);

    @EventListener
    @Async
    public void handleSystemHealthDegraded(SystemHealthDegradedEvent event) {
        logger.warn("Handling SystemHealthDegradedEvent: System {} changed from {} to {} - Reason: {}", 
            event.getSystemId(), 
            event.getPreviousStatus(), 
            event.getCurrentStatus(),
            event.getReason());

        if (event.isSystemDown()) {
            logger.error("🔴 SYSTEM DOWN: {} is currently DOWN", event.getSystemId());
        } else if (event.isSignificantChange()) {
            logger.error("⚠️ SIGNIFICANT DEGRADATION: System {} health degraded significantly", event.getSystemId());
        }
    }
}
