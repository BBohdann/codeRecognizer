package com.example.coderecognizer.controller;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(LocalDateTime timestamp, int status, List<String> errors) {
}