package com.example.coderecognizer.service.exeption;

public class EmptyImageException extends Exception {
    private static final String EMPTY_IMAGE_EXCEPTION_TEXT = "This image is empty";

    public EmptyImageException() {
        super(EMPTY_IMAGE_EXCEPTION_TEXT);
    }
}
