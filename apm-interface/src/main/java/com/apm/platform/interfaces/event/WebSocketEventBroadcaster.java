package com.apm.platform.interfaces.event;

import com.apm.platform.domain.event.AlertTriggeredEvent;
import com.apm.platform.domain.event.MetricCollectedEvent;
import com.apm.platform.domain.event.SystemHealthDegradedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketEventBroadcaster {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventBroadcaster.class);

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    @Async
    public void handleMetricCollected(MetricCollectedEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "METRIC_COLLECTED");
        payload.put("metricId", event.getMetricId());
        payload.put("systemId", event.getSystemId());
        payload.put("latencyMs", event.getLatencyMs());
        payload.put("statusCode", event.getStatusCode());
        payload.put("timestamp", event.getOccurredAt());

        messagingTemplate.convertAndSend("/topic/dashboard/metrics", payload);
        logger.debug("Broadcasted metric collected event via WebSocket");
    }

    @EventListener
    @Async
    public void handleAlertTriggered(AlertTriggeredEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ALERT_TRIGGERED");
        payload.put("alertId", event.getAlertId());
        payload.put("systemId", event.getSystemId());
        payload.put("severity", event.getSeverity());
        payload.put("message", event.getMessage());
        payload.put("timestamp", event.getOccurredAt());

        messagingTemplate.convertAndSend("/topic/dashboard/alerts", payload);
        logger.info("Broadcasted alert triggered event via WebSocket");
    }

    @EventListener
    @Async
    public void handleSystemHealthDegraded(SystemHealthDegradedEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "HEALTH_DEGRADED");
        payload.put("systemId", event.getSystemId());
        payload.put("previousStatus", event.getPreviousStatus());
        payload.put("currentStatus", event.getCurrentStatus());
        payload.put("reason", event.getReason());
        payload.put("timestamp", event.getOccurredAt());

        messagingTemplate.convertAndSend("/topic/dashboard/health", payload);
        logger.warn("Broadcasted health degraded event via WebSocket");
    }
}
