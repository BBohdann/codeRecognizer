package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.mapper.ProductCodeMapper;
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

/**
 * Service responsible for orchestrating the process of decoding, analyzing,
 * and storing barcode information, along with scan metadata.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeRecordService {

    private final CodeDecryptor decryptor;
    private final ValueTypeFinder valueTypeFinder;
    private final ProductCodeService productService;
    private final ScanInfoService scanInfoService;
    private final ProductCodeMapper productCodeMapper;
    private final ObjectMapper objectMapper;

    /**
     * Processes the uploaded image file by decoding the barcode, analyzing the value type,
     * and saving product code and scan information to the database.
     *
     * @param file the image file containing the barcode
     * @return the cleaned code value
     * @throws InvalidImageFormatException if the image is not a valid format
     * @throws IOException if an error occurs during processing
     * @throws EmptyImageException if the uploaded file is empty
     */
    public String process(MultipartFile file) throws InvalidImageFormatException, IOException, EmptyImageException {
        String decrypted = decryptor.decryptCode(file);
        log.debug("Decrypted result: {}", decrypted);

        String codeType = valueTypeFinder.extractCodeType(decrypted);
        String codeValueRaw = valueTypeFinder.extractCodeValue(decrypted);

        ValueType valueType = valueTypeFinder.analyze(codeValueRaw);
        log.debug("Detected value type: {}", valueType);
        String codeValue = cleanCodeValue(codeValueRaw, valueType);

        ProductCodeDto savedProduct = saveProductCode(codeType, codeValue, file.getOriginalFilename());
        saveScanInfo(savedProduct, valueType);

        return savedProduct.getCodeValue();
    }

    /**
     * Cleans the raw code value depending on the detected value type.
     * If the value is JSON, it attempts to parse and reformat it.
     *
     * @param rawValue the raw extracted code value
     * @param valueType the determined value type
     * @return the cleaned code value
     */
    private String cleanCodeValue(String rawValue, ValueType valueType) {
        if (valueType == ValueType.JSON) {
            return parseJson(rawValue);
        }
        return rawValue.trim();
    }

    /**
     * Parses a JSON-formatted code value and normalizes it.
     *
     * @param jsonString the raw JSON string
     * @return a formatted JSON string
     */
    private String parseJson(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON code value", e);
        }
    }

    /**
     * Saves the product code to the database.
     *
     * @param codeType the type of code (e.g. QR, EAN)
     * @param codeValue the actual code value
     * @param fileName the name of the uploaded file
     * @return the saved product code DTO
     */
    private ProductCodeDto saveProductCode(String codeType, String codeValue, String fileName) {
        ProductCodeDto dto = new ProductCodeDto();
        dto.setCodeType(codeType);
        dto.setCodeValue(codeValue);
        dto.setFileName(fileName);
        return productService.save(dto);
    }

    /**
     * Stores metadata about the scan, such as timestamp and success flag.
     *
     * @param productCode the related product code entry
     * @param valueType the value type that was detected
     */
    private void saveScanInfo(ProductCodeDto productCode, ValueType valueType) {
        ScanInfoDto scanInfoDto = new ScanInfoDto();
        scanInfoDto.setValueType(valueType);
        scanInfoDto.setScanDateTime(LocalDateTime.now());
        scanInfoDto.setProductCode(productCodeMapper.toProductCodeEntity(productCode));
        scanInfoDto.setSuccess(true);
        scanInfoService.save(scanInfoDto);
    }
}