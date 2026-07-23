package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.service.CodeDecryptor;
import com.example.coderecognizer.service.service.CodeRecordService;
import com.example.coderecognizer.service.utils.ValueType;
import com.example.coderecognizer.service.utils.ValueTypeFinder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import com.example.coderecognizer.service.dto.DecodedCode;
import com.example.coderecognizer.service.exeption.BarcodeDecodingException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeRecordServiceTest {
    @Mock
    private CodeDecryptor decryptor;
    @Mock
    private ValueTypeFinder valueTypeFinder;
    @Mock
    private ProductCodeService productService;
    @Mock
    private ScanInfoService scanInfoService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CodeRecordService service;
    private final MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[]{1});

    @BeforeEach
    void setUp() {
        service = new CodeRecordService(decryptor, valueTypeFinder, productService, scanInfoService, objectMapper);
    }

    @Test
    void process_TextValue_TrimsAndSavesProductAndScanInfo() throws Exception {
        when(decryptor.decryptCode(file)).thenReturn(new DecodedCode("QR Code", "  hello world  "));
        when(valueTypeFinder.analyze("  hello world  ")).thenReturn(ValueType.TEXT);

        ProductCodeDto savedDto = new ProductCodeDto();
        savedDto.setId(1);
        savedDto.setCodeType("QR Code");
        savedDto.setCodeValue("hello world");
        when(productService.save(any(ProductCodeDto.class))).thenReturn(savedDto);

        String result = service.process(file);

        assertThat(result).isEqualTo("hello world");

        ArgumentCaptor<ProductCodeDto> productCaptor = ArgumentCaptor.forClass(ProductCodeDto.class);
        verify(productService).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getCodeValue()).isEqualTo("hello world");
        assertThat(productCaptor.getValue().getFileName()).isEqualTo("test.png");

        ArgumentCaptor<ScanInfoDto> scanCaptor = ArgumentCaptor.forClass(ScanInfoDto.class);
        verify(scanInfoService).save(scanCaptor.capture());
        assertThat(scanCaptor.getValue().getProductCodeId()).isEqualTo(1);
        assertThat(scanCaptor.getValue().getSuccess()).isTrue();
        assertThat(scanCaptor.getValue().getValueType()).isEqualTo(ValueType.TEXT);
    }

    @Test
    void process_JsonValue_NormalizesJsonBeforeSaving() throws Exception {
        String rawJson = "{ \"a\" :   1 }";
        when(decryptor.decryptCode(file)).thenReturn(new DecodedCode("QR Code", rawJson));
        when(valueTypeFinder.analyze(rawJson)).thenReturn(ValueType.JSON);

        ProductCodeDto savedDto = new ProductCodeDto();
        savedDto.setId(2);
        savedDto.setCodeValue("{\"a\":1}");
        when(productService.save(any(ProductCodeDto.class))).thenReturn(savedDto);

        String result = service.process(file);

        assertThat(result).isEqualTo("{\"a\":1}");
        ArgumentCaptor<ProductCodeDto> captor = ArgumentCaptor.forClass(ProductCodeDto.class);
        verify(productService).save(captor.capture());
        assertThat(captor.getValue().getCodeValue()).isEqualTo("{\"a\":1}");
    }

    @Test
    void process_UrlValue_TrimsWhitespace() throws Exception {
        when(decryptor.decryptCode(file)).thenReturn(new DecodedCode("QR Code", "  https://example.com  "));
        when(valueTypeFinder.analyze("  https://example.com  ")).thenReturn(ValueType.URL);

        ProductCodeDto savedDto = new ProductCodeDto();
        savedDto.setId(3);
        savedDto.setCodeValue("https://example.com");
        when(productService.save(any(ProductCodeDto.class))).thenReturn(savedDto);

        service.process(file);

        ArgumentCaptor<ProductCodeDto> captor = ArgumentCaptor.forClass(ProductCodeDto.class);
        verify(productService).save(captor.capture());
        assertThat(captor.getValue().getCodeValue()).isEqualTo("https://example.com");
    }

    @Test
    void process_InvalidJson_ThrowsRuntimeException() throws Exception {
        String brokenJson = "{ invalid";
        when(decryptor.decryptCode(file)).thenReturn(new DecodedCode("QR Code", brokenJson));
        when(valueTypeFinder.analyze(brokenJson)).thenReturn(ValueType.JSON);

        assertThatThrownBy(() -> service.process(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to parse JSON code value");
    }

    @Test
    void process_DecryptionFails_PropagatesException() throws Exception {
        when(decryptor.decryptCode(file))
                .thenThrow(new BarcodeDecodingException("no code", null));

        assertThatThrownBy(() -> service.process(file)).isInstanceOf(BarcodeDecodingException.class);
    }
}