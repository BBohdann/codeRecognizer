package com.example.coderecognizer.service.exeption;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String barcode) {
        super("No product information found for code: " + barcode);
    }
}