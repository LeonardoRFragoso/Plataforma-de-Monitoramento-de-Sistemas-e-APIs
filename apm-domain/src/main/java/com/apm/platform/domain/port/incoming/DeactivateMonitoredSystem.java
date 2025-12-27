package com.apm.platform.domain.port.incoming;

public interface DeactivateMonitoredSystem {
    void execute(String systemId);
}
