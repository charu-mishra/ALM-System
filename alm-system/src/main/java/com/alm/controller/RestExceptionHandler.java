package com.alm.controller;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseError(SQLException exception) {
        logger.error("Oracle database operation failed", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Database operation failed",
                "details", exception.getMessage() == null ? "Unknown database error" : exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonReadError(HttpMessageNotReadableException exception) {
        logger.error("JSON request body could not be parsed", exception);
        Throwable cause = exception.getMostSpecificCause();
        String details = cause == null ? exception.getMessage() : cause.getMessage();
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Malformed JSON request",
                "details", details
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
}