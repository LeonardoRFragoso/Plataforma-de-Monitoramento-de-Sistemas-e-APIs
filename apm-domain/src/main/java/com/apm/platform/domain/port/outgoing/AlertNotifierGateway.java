package com.apm.platform.domain.port.outgoing;

import com.apm.platform.domain.entity.Alert;

public interface AlertNotifierGateway {
    void notifyAlert(Alert alert);
    void notifyAlertResolved(Alert alert);
}
