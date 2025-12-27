package com.apm.platform.interfaces.rest.exception;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    private final String type;
    private final String title;
    private final int status;
    private final String detail;
    private final String instance;
    private final Instant timestamp;
    private final List<String> errors;

    public ErrorResponse(String type, String title, int status, String detail, 
                        String instance, List<String> errors) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.timestamp = Instant.now();
        this.errors = errors;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }
}
