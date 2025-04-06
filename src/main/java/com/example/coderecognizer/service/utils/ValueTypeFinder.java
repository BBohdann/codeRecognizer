package com.example.coderecognizer.service.utils;

import com.example.coderecognizer.service.exeption.InvalidDecryptionFormatException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Utility service for analyzing the structure and type of decoded barcode values.
 * Can classify values as JSON, URL, or plain text, and extract type/value components from a combined string.
 */
@Slf4j
@Service
public class ValueTypeFinder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Analyzes a given string to determine its value type.
     *
     * @param fileValue the decoded value to analyze
     * @return the detected {@link ValueType}
     */
    public ValueType analyze(String fileValue) {
        log.debug("Analyzing value: {}", fileValue);

        if (isURL(fileValue)) return ValueType.URL;
        if (isValidJson(fileValue)) return ValueType.JSON;

        return ValueType.TEXT;
    }

    /**
     * Determines whether the given string is valid JSON.
     *
     * @param value the string to test
     * @return true if the string is valid JSON
     */
    private boolean isValidJson(String value) {
        try {
            objectMapper.readTree(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks whether the given string appears to be a URL.
     *
     * @param str the string to check
     * @return true if the string starts with a known URL prefix
     */
    private boolean isURL(String str) {
        String lower = str.toLowerCase();
        return lower.startsWith("http://") ||
                lower.startsWith("https://") ||
                lower.startsWith("ftp://") ||
                lower.startsWith("sftp://") ||
                lower.startsWith("ftps://") ||
                lower.startsWith("www.") ||
                lower.startsWith("localhost");
    }

    /**
     * Extracts the code value from a formatted decrypted string.
     * Expects a string in the format "TYPE: VALUE".
     *
     * @param decrypted the decrypted string
     * @return the code value portion
     * @throws InvalidDecryptionFormatException if the format is invalid
     */
    public String extractCodeValue(String decrypted) {
        String[] parts = decrypted.split(": ", 2);
        if (parts.length < 2) {
            throw new InvalidDecryptionFormatException("Invalid decrypted format: " + decrypted);
        }
        return parts[1];
    }

    /**
     * Extracts the code type from a formatted decrypted string.
     * Expects a string in the format "TYPE: VALUE".
     *
     * @param decrypted the decrypted string
     * @return the code type portion
     * @throws InvalidDecryptionFormatException if the format is invalid
     */
    public String extractCodeType(String decrypted) {
        String[] parts = decrypted.split(": ", 2);
        if (parts.length < 2) {
            throw new InvalidDecryptionFormatException("Invalid decrypted format: " + decrypted);
        }
        return parts[0];
    }
}
