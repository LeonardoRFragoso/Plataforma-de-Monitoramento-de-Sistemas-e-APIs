package com.apm.platform.domain.exception;

public class InvalidSystemStateException extends DomainException {
    
    public InvalidSystemStateException(String message) {
        super(message);
    }

    public InvalidSystemStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
