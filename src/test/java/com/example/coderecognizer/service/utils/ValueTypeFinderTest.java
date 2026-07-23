package com.example.coderecognizer.service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValueTypeFinderTest {
    private final ValueTypeFinder finder = new ValueTypeFinder(new ObjectMapper());

    @ParameterizedTest
    @ValueSource(strings = {
            "http://example.com",
            "https://example.com/path",
            "www.example.com",
            "ftp://files.example.com",
            "sftp://files.example.com",
            "ftps://files.example.com",
            "localhost:8080"
    })
    void analyze_UrlLikeValues_ReturnsUrl(String value) {
        assertThat(finder.analyze(value)).isEqualTo(ValueType.URL);
    }

    @Test
    void analyze_JsonObject_ReturnsJson() {
        assertThat(finder.analyze("{\"key\":\"value\"}")).isEqualTo(ValueType.JSON);
    }

    @Test
    void analyze_JsonArray_ReturnsJson() {
        assertThat(finder.analyze("[1, 2, 3]")).isEqualTo(ValueType.JSON);
    }

    @Test
    void analyze_MalformedJsonLookingValue_ReturnsText() {
        assertThat(finder.analyze("{not valid json}")).isEqualTo(ValueType.TEXT);
    }

    @Test
    void analyze_PlainText_ReturnsText() {
        assertThat(finder.analyze("just some scanned text")).isEqualTo(ValueType.TEXT);
    }

    @Test
    void analyze_UppercaseUrlPrefix_IsCaseInsensitive() {
        assertThat(finder.analyze("HTTPS://EXAMPLE.COM")).isEqualTo(ValueType.URL);
    }
}
