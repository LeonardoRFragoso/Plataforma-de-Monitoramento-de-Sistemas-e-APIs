package com.apm.platform.domain.port.incoming;

public interface ResolveAlert {
    void execute(String alertId, String notes);
}
