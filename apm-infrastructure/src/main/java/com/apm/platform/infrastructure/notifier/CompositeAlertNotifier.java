package com.apm.platform.infrastructure.notifier;

import com.apm.platform.domain.entity.Alert;
import com.apm.platform.domain.port.outgoing.AlertNotifierGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class CompositeAlertNotifier implements AlertNotifierGateway {

    private static final Logger logger = LoggerFactory.getLogger(CompositeAlertNotifier.class);

    private final List<AlertNotifierGateway> notifiers;

    public CompositeAlertNotifier(List<AlertNotifierGateway> notifiers) {
        this.notifiers = notifiers.stream()
            .filter(n -> !(n instanceof CompositeAlertNotifier))
            .toList();
        
        logger.info("CompositeAlertNotifier initialized with {} notifier(s)", this.notifiers.size());
    }

    @Override
    public void notifyAlert(Alert alert) {
        for (AlertNotifierGateway notifier : notifiers) {
            try {
                notifier.notifyAlert(alert);
            } catch (Exception e) {
                logger.error("Failed to notify alert using {}", notifier.getClass().getSimpleName(), e);
            }
        }
    }

    @Override
    public void notifyAlertResolved(Alert alert) {
        for (AlertNotifierGateway notifier : notifiers) {
            try {
                notifier.notifyAlertResolved(alert);
            } catch (Exception e) {
                logger.error("Failed to notify alert resolution using {}", notifier.getClass().getSimpleName(), e);
            }
        }
    }
}
