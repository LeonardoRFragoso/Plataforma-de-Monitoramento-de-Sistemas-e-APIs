package com.apm.platform.infrastructure.event;

import com.apm.platform.domain.event.MetricCollectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MetricCollectedEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MetricCollectedEventListener.class);

    @EventListener
    @Async
    public void handleMetricCollected(MetricCollectedEvent event) {
        logger.debug("Handling MetricCollectedEvent: Metric {} collected for system {} - Latency: {}ms, Status: {}", 
            event.getMetricId(), 
            event.getSystemId(),
            event.getLatencyMs(),
            event.getStatusCode());

        if (event.hasError()) {
            logger.warn("Metric collected with error for system {}: Status code {}", 
                event.getSystemId(), event.getStatusCode());
        }
    }
}
