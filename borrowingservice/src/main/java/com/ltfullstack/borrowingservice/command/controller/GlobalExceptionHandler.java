package com.ltfullstack.borrowingservice.command.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("=== GLOBAL EXCEPTION ===");
        log.error("Message: " + e.getMessage());
        log.error("Class: " + e.getClass().getName());
        
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
            if (sb.length() > 2000) break;
        }
        log.error("Stack trace:\n" + sb.toString());
        
        if (e.getCause() != null) {
            log.error("Cause: " + e.getCause().getMessage());
            for (StackTraceElement element : e.getCause().getStackTrace()) {
                log.error("  at " + element.toString());
            }
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of(
                        "success", false,
                        "error", e.getMessage(),
                        "type", e.getClass().getSimpleName()
                ));
    }
}
