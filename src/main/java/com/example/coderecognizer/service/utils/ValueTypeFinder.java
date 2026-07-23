package com.example.coderecognizer.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValueTypeFinder {

    private static final String[] URL_PREFIXES = {
            "http://", "https://", "ftp://", "sftp://", "ftps://", "www.", "localhost"
    };

    private final ObjectMapper objectMapper;

    public ValueTypeFinder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ValueType analyze(String value) {
        log.debug("Analyzing value: {}", value);

        if (isUrl(value)) {
            return ValueType.URL;
        }
        if (isValidJson(value)) {
            return ValueType.JSON;
        }
        return ValueType.TEXT;
    }

    private boolean isValidJson(String value) {
        String trimmed = value.trim();
        boolean looksLikeJson = (trimmed.startsWith("{") && trimmed.endsWith("}"))
                || (trimmed.startsWith("[") && trimmed.endsWith("]"));
        if (!looksLikeJson) {
            return false;
        }
        try {
            objectMapper.readTree(trimmed);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isUrl(String value) {
        String lower = value.toLowerCase();
        for (String prefix : URL_PREFIXES) {
            if (lower.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
