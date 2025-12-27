package com.apm.platform.infrastructure.notifier;

import com.apm.platform.domain.entity.Alert;
import com.apm.platform.domain.port.outgoing.AlertNotifierGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogAlertNotifier implements AlertNotifierGateway {

    private static final Logger logger = LoggerFactory.getLogger(LogAlertNotifier.class);

    @Override
    public void notifyAlert(Alert alert) {
        String logMessage = String.format(
            "🚨 ALERT TRIGGERED | System: %s | Severity: %s | Message: %s | Time: %s",
            alert.getSystemId(),
            alert.getSeverity(),
            alert.getMessage(),
            alert.getTriggeredAt()
        );

        if (alert.isCritical()) {
            logger.error(logMessage);
        } else {
            logger.warn(logMessage);
        }
    }

    @Override
    public void notifyAlertResolved(Alert alert) {
        String logMessage = String.format(
            "✅ ALERT RESOLVED | System: %s | Severity: %s | Duration: %s | Notes: %s",
            alert.getSystemId(),
            alert.getSeverity(),
            alert.getDuration(),
            alert.getResolutionNotes()
        );

        logger.info(logMessage);
    }
}
