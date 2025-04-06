package com.example.coderecognizer.service.service.barcode;

import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.service.service.impl.ProductInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BarcodeInfoReturnerTest {

    @Mock
    private ProductInfoService productInfoService;

    @Spy
    @InjectMocks
    private BarcodeInfoReturner barcodeInfoReturner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnProductInfoFromDatabaseIfExists() {
        String barcode = "123456789";
        ProductInfo info = new ProductInfo();
        info.setProductData("Cached product info");

        when(productInfoService.findByCodeValue(barcode)).thenReturn(Optional.of(info));
        String result = barcodeInfoReturner.getProductInfoFromAllAPIs(barcode);

        assertEquals("Cached product info", result);
        verify(productInfoService, never()).saveProductInfo(any(), any(), any());
    }

    @Test
    void shouldCallExternalApiIfNotInDatabaseAndSaveResult() {
        String barcode = "987654321";
        when(productInfoService.findByCodeValue(barcode)).thenReturn(Optional.empty());
        doReturn("External API response").when(barcodeInfoReturner).fetchProductInfo(anyString());

        String result = barcodeInfoReturner.getProductInfoFromAllAPIs(barcode);
        assertEquals("External API response", result);
        verify(productInfoService).saveProductInfo(eq(barcode), eq("External API response"), anyString());
    }

    @Test
    void shouldThrowExceptionWhenNoApiReturnsValidResponse() {
        String barcode = "000000000";
        when(productInfoService.findByCodeValue(barcode)).thenReturn(Optional.empty());
        doReturn(null).when(barcodeInfoReturner).fetchProductInfo(anyString());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> barcodeInfoReturner.getProductInfoFromAllAPIs(barcode));

        assertEquals("Product not found on any API", ex.getMessage());
        verify(productInfoService, never()).saveProductInfo(any(), any(), any());
    }
}
