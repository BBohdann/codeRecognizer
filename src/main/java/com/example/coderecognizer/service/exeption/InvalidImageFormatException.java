package com.example.coderecognizer.service.exeption;

public class InvalidImageFormatException extends Exception {
    private static final String MESSAGE_TEMPLATE = "%s - is not a valid image format. Use JPEG, PNG, or GIF";

    public InvalidImageFormatException(String invalidFormat) {
        super(String.format(MESSAGE_TEMPLATE, invalidFormat));
    }
}
