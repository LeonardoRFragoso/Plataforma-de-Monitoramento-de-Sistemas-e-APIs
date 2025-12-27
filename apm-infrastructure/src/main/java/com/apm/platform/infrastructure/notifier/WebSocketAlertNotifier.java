package com.apm.platform.infrastructure.notifier;

import com.apm.platform.domain.entity.Alert;
import com.apm.platform.domain.port.outgoing.AlertNotifierGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "apm.notifier.websocket.enabled", havingValue = "true", matchIfMissing = false)
public class WebSocketAlertNotifier implements AlertNotifierGateway {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAlertNotifier.class);

    @Override
    public void notifyAlert(Alert alert) {
        logger.info("WebSocket notification (stub): Alert triggered for system {}", alert.getSystemId());
    }

    @Override
    public void notifyAlertResolved(Alert alert) {
        logger.info("WebSocket notification (stub): Alert resolved for system {}", alert.getSystemId());
    }
}
