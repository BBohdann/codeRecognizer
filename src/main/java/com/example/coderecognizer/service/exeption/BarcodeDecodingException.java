package com.example.coderecognizer.service.exeption;

public class BarcodeDecodingException extends RuntimeException {
    public BarcodeDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}