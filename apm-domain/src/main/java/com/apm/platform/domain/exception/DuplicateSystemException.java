package com.apm.platform.domain.exception;

public class DuplicateSystemException extends DomainException {
    
    public DuplicateSystemException(String systemName) {
        super(String.format("A monitored system with name '%s' already exists", systemName));
    }

    public DuplicateSystemException(String systemName, Throwable cause) {
        super(String.format("A monitored system with name '%s' already exists", systemName), cause);
    }
}
