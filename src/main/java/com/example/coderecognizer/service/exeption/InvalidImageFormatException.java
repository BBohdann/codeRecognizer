package com.example.coderecognizer.service.exeption;

public class InvalidImageFormatException extends Exception {
    private static final String INVALID_IMAGE_FORMAT_EXCEPTION_TEXT = " %s - is not valid image format. Use JPEG, PNG, or GIF";

    public InvalidImageFormatException(String invalidFormat) {
            super(String.format(INVALID_IMAGE_FORMAT_EXCEPTION_TEXT , invalidFormat));
    }
}
