package com.example.coderecognizer.service.exeption;

public class EmptyImageException extends Exception {
    private static final String MESSAGE = "This image is empty";

    public EmptyImageException() {
        super(MESSAGE);
    }
}
