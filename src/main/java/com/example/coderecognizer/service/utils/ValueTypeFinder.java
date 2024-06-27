package com.example.coderecognizer.service.utils;

import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.service.CodeDecryptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ValueTypeFinder {
    private final CodeDecryptor decryptor;

    public ValueType analyzeFileType(MultipartFile file) throws InvalidImageFormatException {
        String fileContent = decryptor.decryptCode(file);
        String[] parts = fileContent.split(": ", 2);
        String fileValue = parts[1];
        System.out.println("fileContent = " + fileContent);
        if (isURL(fileValue)) {
            return ValueType.URL;
        }
        else if(isValidJSON(fileValue))
            return ValueType.JSON;
        else return ValueType.TEXT;
    }

    private boolean isValidJSON(String json) {
        boolean valid = true;
        try {
            new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            valid = false;
        }
        return valid;
    }

    private boolean isURL(String str) {
        String lowerCaseStr = str.toLowerCase();
        return lowerCaseStr.startsWith("http://") ||
                lowerCaseStr.startsWith("https://") ||
                lowerCaseStr.startsWith("ftp://") ||
                lowerCaseStr.startsWith("sftp://") ||
                lowerCaseStr.startsWith("ftps://") ||
                lowerCaseStr.startsWith("www.") ||
                lowerCaseStr.startsWith("localhost");
    }
}