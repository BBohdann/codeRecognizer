package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.dto.DecodedCode;
import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.service.impl.ProductCodeService;
import com.example.coderecognizer.service.service.impl.ScanInfoService;
import com.example.coderecognizer.service.utils.ValueType;
import com.example.coderecognizer.service.utils.ValueTypeFinder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeRecordService {

    private final CodeDecryptor decryptor;
    private final ValueTypeFinder valueTypeFinder;
    private final ProductCodeService productService;
    private final ScanInfoService scanInfoService;
    private final ObjectMapper objectMapper;

    public String process(MultipartFile file) throws InvalidImageFormatException, IOException, EmptyImageException {
        DecodedCode decoded = decryptor.decryptCode(file);
        log.debug("Decoded result: {}", decoded);

        ValueType valueType = valueTypeFinder.analyze(decoded.codeValue());
        log.debug("Detected value type: {}", valueType);
        String codeValue = cleanCodeValue(decoded.codeValue(), valueType);

        ProductCodeDto savedProduct = saveProductCode(decoded.codeType(), codeValue, file.getOriginalFilename());
        saveScanInfo(savedProduct.getId(), valueType);

        return savedProduct.getCodeValue();
    }

    private String cleanCodeValue(String rawValue, ValueType valueType) {
        if (valueType == ValueType.JSON) {
            return parseJson(rawValue);
        }
        return rawValue.trim();
    }

    private String parseJson(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON code value", e);
        }
    }

    private ProductCodeDto saveProductCode(String codeType, String codeValue, String fileName) {
        ProductCodeDto dto = new ProductCodeDto();
        dto.setCodeType(codeType);
        dto.setCodeValue(codeValue);
        dto.setFileName(fileName);
        return productService.save(dto);
    }

    private void saveScanInfo(Integer productCodeId, ValueType valueType) {
        ScanInfoDto scanInfoDto = new ScanInfoDto();
        scanInfoDto.setValueType(valueType);
        scanInfoDto.setScanDateTime(LocalDateTime.now());
        scanInfoDto.setProductCodeId(productCodeId);
        scanInfoDto.setSuccess(true);
        scanInfoService.save(scanInfoDto);
    }
}