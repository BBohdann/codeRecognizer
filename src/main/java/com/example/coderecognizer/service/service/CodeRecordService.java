package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.mapper.ProductCodeMapper;
import com.example.coderecognizer.service.service.barcode.BarcodeInfoReturner;
import com.example.coderecognizer.service.service.impl.ProductCodeService;
import com.example.coderecognizer.service.service.impl.ScanInfoService;
import com.example.coderecognizer.service.utils.ValueType;
import com.example.coderecognizer.service.utils.ValueTypeFinder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CodeRecordService {
    private final ValueTypeFinder valueTypeFinder;
    private final CodeDecryptor decryptor;
    private final ProductCodeService productService;
    private final ScanInfoService scanInfoService;
    private final ProductCodeMapper productCodeMapper;

    public String proccess (MultipartFile file) throws InvalidImageFormatException {
        String decryptedResult = decryptor.decryptCode(file);
        ValueType valueType = valueTypeFinder.analyzeFileType(file);

        String[] parts = decryptedResult.split(": ", 2);
        if (parts.length < 2)
            throw new RuntimeException("Invalid decrypted result format");
        String codeValue;
        String codeType = parts[0];
        codeValue = parts[1];
        if(valueType == ValueType.JSON)
            codeValue = parseJson(codeValue);

        ProductCodeDto productCode = new ProductCodeDto();
        productCode.setCodeType(codeType);
        productCode.setCodeValue(codeValue);
        productCode.setFileName(file.getOriginalFilename());
        ProductCodeDto saved = productService.save(productCode);

        ScanInfoDto scanInfoDto = new ScanInfoDto();
        scanInfoDto.setValueType(valueType);
        scanInfoDto.setScanDateTime(LocalDateTime.now());
        scanInfoDto.setProductCode(productCodeMapper.toProductCodeEntity(saved));
        scanInfoDto.setSuccess(true);
        scanInfoService.save(scanInfoDto);
        return productCode.getCodeValue();
    }

    private String  parseJson(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            String json = objectMapper.treeToValue(jsonNode, String.class);

            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}