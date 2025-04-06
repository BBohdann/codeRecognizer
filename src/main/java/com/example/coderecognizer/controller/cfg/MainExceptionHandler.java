package com.example.coderecognizer.controller.cfg;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Global exception handler for the application.
 * Handles specific and general exceptions and returns appropriate HTTP responses.
 */
@RestControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<String>>> handleGenericException(Exception ex) {
        return buildErrorResponse(ex.getMessage());
    }

    private ResponseEntity<Map<String, List<String>>> buildErrorResponse(String message) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", Collections.singletonList(message));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}