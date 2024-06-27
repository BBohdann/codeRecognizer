package com.example.coderecognizer.controller.cfg;

import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Головний обробник виключень для додатка
 * Цей клас визначає глобальні хендлери виключень для обробки специфічних виключень,
 * таких як InvalidUrlException, і повертає відповідні відповіді клієнту з відповідним HTTP статусом.
 */
@RestControllerAdvice
public class MainExceptionHandler {
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Map<String, List<String>>> anotherException(Exception ex) {
        return getErorMap(ex);
    }

    private ResponseEntity<Map<String, List<String>>> getErorMap(Exception ex) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("errors", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}