package com.example.coderecognizer.service.service.barcode;

import com.example.coderecognizer.controller.cfg.BarcodeApiProperties;
import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.service.exeption.ProductNotFoundException;
import com.example.coderecognizer.service.service.impl.ProductInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarcodeInfoReturnerTest {

    @Mock
    private ProductInfoService productInfoService;
    @Mock
    private RestTemplate restTemplate;

    private BarcodeInfoReturner barcodeInfoReturner;

    @BeforeEach
    void setUp() {
        BarcodeApiProperties apiProperties = new BarcodeApiProperties();
        Map<String, String> apis = new LinkedHashMap<>();
        apis.put("api1", "http://api1.example.com/%s");
        apis.put("api2", "http://api2.example.com/%s");
        apiProperties.setApis(apis);

        barcodeInfoReturner = new BarcodeInfoReturner(productInfoService, restTemplate, apiProperties);
    }

    @Test
    void getProductInfoFromAllAPIs_CachedValueExists_ReturnsCachedDataWithoutCallingApis() {
        ProductInfo cached = new ProductInfo();
        cached.setProductData("{\"cached\":true}");
        when(productInfoService.findByCodeValue("123")).thenReturn(Optional.of(cached));

        String result = barcodeInfoReturner.getProductInfoFromAllAPIs("123");

        assertThat(result).isEqualTo("{\"cached\":true}");
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getProductInfoFromAllAPIs_FirstApiSucceeds_CachesAndReturnsResult() {
        when(productInfoService.findByCodeValue("123")).thenReturn(Optional.empty());
        when(restTemplate.getForObject("http://api1.example.com/123", String.class))
                .thenReturn("{\"name\":\"Cola\"}");

        String result = barcodeInfoReturner.getProductInfoFromAllAPIs("123");

        assertThat(result).isEqualTo("{\"name\":\"Cola\"}");
        verify(productInfoService).saveProductInfo("123", "{\"name\":\"Cola\"}", "api1");
        verify(restTemplate, never()).getForObject(eq("http://api2.example.com/123"), eq(String.class));
    }

    @Test
    void getProductInfoFromAllAPIs_FirstApiReturnsNotFoundMarker_FallsBackToSecondApi() {
        when(productInfoService.findByCodeValue("123")).thenReturn(Optional.empty());
        when(restTemplate.getForObject("http://api1.example.com/123", String.class))
                .thenReturn("{\"status\":0}");
        when(restTemplate.getForObject("http://api2.example.com/123", String.class))
                .thenReturn("{\"name\":\"Cola\"}");

        String result = barcodeInfoReturner.getProductInfoFromAllAPIs("123");

        assertThat(result).isEqualTo("{\"name\":\"Cola\"}");
        verify(productInfoService).saveProductInfo("123", "{\"name\":\"Cola\"}", "api2");
    }

    @Test
    void getProductInfoFromAllAPIs_AllApisFail_ThrowsProductNotFoundException() {
        when(productInfoService.findByCodeValue("123")).thenReturn(Optional.empty());
        when(restTemplate.getForObject("http://api1.example.com/123", String.class)).thenReturn(null);
        when(restTemplate.getForObject("http://api2.example.com/123", String.class)).thenReturn("");

        assertThatThrownBy(() -> barcodeInfoReturner.getProductInfoFromAllAPIs("123"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("123");

        verify(productInfoService, never()).saveProductInfo(any(), any(), any());
    }

    @Test
    void fetchProductInfo_BadRequest_ReturnsEmptyOptional() {
        when(restTemplate.getForObject("http://api1.example.com/x", String.class))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, new byte[0], null));

        Optional<String> result = barcodeInfoReturner.fetchProductInfo("http://api1.example.com/x");

        assertThat(result).isEmpty();
    }

    @Test
    void fetchProductInfo_NetworkFailure_ReturnsEmptyOptional() {
        when(restTemplate.getForObject("http://api1.example.com/x", String.class))
                .thenThrow(new ResourceAccessException("timeout"));

        Optional<String> result = barcodeInfoReturner.fetchProductInfo("http://api1.example.com/x");

        assertThat(result).isEmpty();
    }

    @Test
    void fetchProductInfo_ValidBody_ReturnsOptionalOfBody() {
        when(restTemplate.getForObject("http://api1.example.com/x", String.class))
                .thenReturn("{\"ok\":true}");

        Optional<String> result = barcodeInfoReturner.fetchProductInfo("http://api1.example.com/x");

        assertThat(result).contains("{\"ok\":true}");
    }
}
