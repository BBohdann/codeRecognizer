package com.example.coderecognizer.service.utils;

import com.example.coderecognizer.service.exeption.InvalidDecryptionFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueTypeFinderTest {

    private ValueTypeFinder valueTypeFinder;

    @BeforeEach
    void setUp() {
        valueTypeFinder = new ValueTypeFinder();
    }

    @Test
    void analyze_shouldReturnURL_whenValueIsURL() {
        String url = "https://example.com";
        assertEquals(ValueType.URL, valueTypeFinder.analyze(url));
    }

    @Test
    void analyze_shouldReturnJSON_whenValueIsJson() {
        String json = "{\"name\":\"Test\",\"value\":123}";
        assertEquals(ValueType.JSON, valueTypeFinder.analyze(json));
    }

    @Test
    void analyze_shouldReturnTEXT_whenValueIsPlainText() {
        String text = "just some plain text";
        assertEquals(ValueType.TEXT, valueTypeFinder.analyze(text));
    }

    @Test
    void extractCodeType_shouldReturnTypePart_whenDecryptedIsValid() {
        String decrypted = "QR Code: 123456";
        String result = valueTypeFinder.extractCodeType(decrypted);
        assertEquals("QR Code", result);
    }

    @Test
    void extractCodeType_shouldThrowException_whenFormatIsInvalid() {
        String invalid = "invalid_format_string";

        assertThrows(InvalidDecryptionFormatException.class,
                () -> valueTypeFinder.extractCodeType(invalid));
    }

    @Test
    void extractCodeValue_shouldReturnValuePart_whenDecryptedIsValid() {
        String decrypted = "QR Code: 123456";
        String result = valueTypeFinder.extractCodeValue(decrypted);
        assertEquals("123456", result);
    }

    @Test
    void extractCodeValue_shouldThrowException_whenFormatIsInvalid() {
        String invalid = "invalid_only_code_type";

        assertThrows(InvalidDecryptionFormatException.class,
                () -> valueTypeFinder.extractCodeValue(invalid));
    }
}
