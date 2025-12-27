package com.apm.platform.domain.exception;

public class AlertRuleViolationException extends DomainException {
    
    public AlertRuleViolationException(String message) {
        super(message);
    }

    public AlertRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
