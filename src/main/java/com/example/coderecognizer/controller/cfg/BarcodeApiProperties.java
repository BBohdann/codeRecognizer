package com.example.coderecognizer.controller.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
 
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "barcode")
public class BarcodeApiProperties {
    private Map<String, String> apis = new LinkedHashMap<>();
}