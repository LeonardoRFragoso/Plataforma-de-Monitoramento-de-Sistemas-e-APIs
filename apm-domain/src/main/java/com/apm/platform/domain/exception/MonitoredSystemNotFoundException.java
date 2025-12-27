package com.apm.platform.domain.exception;

public class MonitoredSystemNotFoundException extends DomainException {
    
    public MonitoredSystemNotFoundException(String systemId) {
        super(String.format("Monitored system not found with ID: %s", systemId));
    }

    public MonitoredSystemNotFoundException(String systemId, Throwable cause) {
        super(String.format("Monitored system not found with ID: %s", systemId), cause);
    }
}
