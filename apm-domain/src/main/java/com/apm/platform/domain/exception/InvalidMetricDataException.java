package com.apm.platform.domain.exception;

public class InvalidMetricDataException extends DomainException {
    
    public InvalidMetricDataException(String message) {
        super(message);
    }

    public InvalidMetricDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
