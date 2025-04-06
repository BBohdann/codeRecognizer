package com.example.coderecognizer.service.exeption;

public class InvalidDecryptionFormatException extends RuntimeException {
    public InvalidDecryptionFormatException(String message) {
        super(message);
    }
}