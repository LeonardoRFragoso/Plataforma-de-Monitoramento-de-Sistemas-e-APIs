package com.apm.platform.interfaces.rest.exception;

import com.apm.platform.domain.exception.DomainException;
import com.apm.platform.domain.exception.DuplicateSystemException;
import com.apm.platform.domain.exception.MonitoredSystemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MonitoredSystemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMonitoredSystemNotFound(
            MonitoredSystemNotFoundException ex, WebRequest request) {
        
        logger.warn("Monitored system not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            "/errors/not-found",
            "Resource Not Found",
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""),
            Collections.emptyList()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateSystemException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateSystem(
            DuplicateSystemException ex, WebRequest request) {
        
        logger.warn("Duplicate system: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            "/errors/conflict",
            "Resource Conflict",
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""),
            Collections.emptyList()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.warn("Invalid argument: {}", ex.getMessage());

        List<String> errors = ex.getMessage().contains("Validation failed:") 
            ? List.of(ex.getMessage().replace("Validation failed: ", "").split(", "))
            : List.of(ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            "/errors/bad-request",
            "Bad Request",
            HttpStatus.BAD_REQUEST.value(),
            "Invalid request parameters",
            request.getDescription(false).replace("uri=", ""),
            errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, WebRequest request) {
        
        logger.error("Domain exception: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            "/errors/business-rule",
            "Business Rule Violation",
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""),
            Collections.emptyList()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected error", ex);

        ErrorResponse error = new ErrorResponse(
            "/errors/internal-server-error",
            "Internal Server Error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false).replace("uri=", ""),
            Collections.emptyList()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
