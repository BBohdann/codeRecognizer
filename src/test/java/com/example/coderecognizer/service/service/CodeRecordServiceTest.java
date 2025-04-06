package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.mapper.ProductCodeMapper;
import com.example.coderecognizer.service.service.impl.ProductCodeService;
import com.example.coderecognizer.service.service.impl.ScanInfoService;
import com.example.coderecognizer.service.utils.ValueType;
import com.example.coderecognizer.service.utils.ValueTypeFinder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CodeRecordServiceTest {

    private CodeDecryptor decryptor;
    private ValueTypeFinder valueTypeFinder;
    private ProductCodeService productService;
    private ScanInfoService scanInfoService;
    private ProductCodeMapper productCodeMapper;
    private ObjectMapper objectMapper;
    private CodeRecordService codeRecordService;

    @BeforeEach
    void setUp() {
        decryptor = mock(CodeDecryptor.class);
        valueTypeFinder = mock(ValueTypeFinder.class);
        productService = mock(ProductCodeService.class);
        scanInfoService = mock(ScanInfoService.class);
        productCodeMapper = mock(ProductCodeMapper.class);
        objectMapper = new ObjectMapper();
        codeRecordService = new CodeRecordService(decryptor, valueTypeFinder, productService, scanInfoService, productCodeMapper, objectMapper);
    }

    @Test
    void shouldProcessCodeCorrectly() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "file.png", "image/png", new byte[0]);

        String decryptedCode = "QR Code: 1234567890123";
        String rawValue = "1234567890123";
        String cleanedValue = "1234567890123";

        when(decryptor.decryptCode(file)).thenReturn(decryptedCode);
        when(valueTypeFinder.extractCodeType(decryptedCode)).thenReturn("QR Code");
        when(valueTypeFinder.extractCodeValue(decryptedCode)).thenReturn(rawValue);
        when(valueTypeFinder.analyze(rawValue)).thenReturn(ValueType.TEXT);

        ProductCodeDto productDto = new ProductCodeDto();
        productDto.setCodeType("QR Code");
        productDto.setCodeValue(cleanedValue);
        productDto.setFileName("file.png");

        when(productService.save(any(ProductCodeDto.class))).thenReturn(productDto);
        when(productCodeMapper.toProductCodeEntity(productDto)).thenReturn(null);

        String result = codeRecordService.process(file);

        assertEquals(cleanedValue, result);

        ArgumentCaptor<ScanInfoDto> captor = ArgumentCaptor.forClass(ScanInfoDto.class);
        verify(scanInfoService, times(1)).save(captor.capture());
        assertEquals(ValueType.TEXT, captor.getValue().getValueType());
        assertEquals(true, captor.getValue().getSuccess());
    }

    @Test
    void shouldCleanJsonCorrectly() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "data.json", "application/json", new byte[0]);

        String decrypted = "QR Code: {\"name\":\"Test\",\"id\":1}";
        String rawValue = "{\"name\":\"Test\",\"id\":1}";

        when(decryptor.decryptCode(file)).thenReturn(decrypted);
        when(valueTypeFinder.extractCodeType(decrypted)).thenReturn("QR Code");
        when(valueTypeFinder.extractCodeValue(decrypted)).thenReturn(rawValue);
        when(valueTypeFinder.analyze(rawValue)).thenReturn(ValueType.JSON);

        ProductCodeDto productDto = new ProductCodeDto();
        productDto.setCodeType("QR Code");
        productDto.setFileName("data.json");
        productDto.setCodeValue(objectMapper.writeValueAsString(objectMapper.readTree(rawValue)));

        when(productService.save(any(ProductCodeDto.class))).thenReturn(productDto);
        when(productCodeMapper.toProductCodeEntity(productDto)).thenReturn(null);

        String result = codeRecordService.process(file);

        assertEquals(productDto.getCodeValue(), result);
        verify(scanInfoService, times(1)).save(any(ScanInfoDto.class));
    }
}