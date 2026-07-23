package com.example.coderecognizer.integration;

import com.example.coderecognizer.controller.controller.CodeController;
import com.example.coderecognizer.service.exeption.BarcodeDecodingException;
import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.exeption.ProductNotFoundException;
import com.example.coderecognizer.service.service.CodeRecordService;
import com.example.coderecognizer.service.service.barcode.BarcodeInfoReturner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CodeController.class)
class CodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CodeRecordService codeRecordService;

    @MockBean
    private BarcodeInfoReturner barcodeInfoReturner;

    @Test
    void decode_ValidImage_Returns200WithDecodedValue() throws Exception {
        when(codeRecordService.process(any())).thenReturn("4006381333931");

        MockMultipartFile file = new MockMultipartFile("file", "barcode.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/v1/codes/recognize").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("4006381333931"));
    }

    @Test
    void decode_InvalidImageFormat_Returns400() throws Exception {
        when(codeRecordService.process(any())).thenThrow(new InvalidImageFormatException("bmp"));

        MockMultipartFile file = new MockMultipartFile("file", "barcode.bmp", "image/bmp", new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/v1/codes/recognize").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("bmp - is not a valid image format. Use JPEG, PNG, or GIF"));
    }

    @Test
    void decode_EmptyImage_Returns400() throws Exception {
        when(codeRecordService.process(any())).thenThrow(new EmptyImageException());

        MockMultipartFile file = new MockMultipartFile("file", "empty.png", MediaType.IMAGE_PNG_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/v1/codes/recognize").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("This image is empty"));
    }

    @Test
    void decode_NoBarcodeInImage_Returns422() throws Exception {
        when(codeRecordService.process(any()))
                .thenThrow(new BarcodeDecodingException("No barcode or QR code could be found in this image", null));

        MockMultipartFile file = new MockMultipartFile("file", "blank.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1});

        mockMvc.perform(multipart("/api/v1/codes/recognize").file(file))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkProductOnOpenFood_ExistingProduct_Returns200WithJson() throws Exception {
        when(barcodeInfoReturner.getProductInfoFromAllAPIs("4006381333931"))
                .thenReturn("{\"product\":{\"name\":\"Cola\"}}");

        mockMvc.perform(get("/api/v1/products").param("codeInfo", "4006381333931"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"product\":{\"name\":\"Cola\"}}"));
    }

    @Test
    void checkProductOnOpenFood_UnknownProduct_Returns404() throws Exception {
        when(barcodeInfoReturner.getProductInfoFromAllAPIs("000000"))
                .thenThrow(new ProductNotFoundException("000000"));

        mockMvc.perform(get("/api/v1/products").param("codeInfo", "000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value("No product information found for code: 000000"));
    }

    @Test
    void decodeAndGetProductInfo_ValidImage_Returns200WithDownloadHeaders() throws Exception {
        when(codeRecordService.process(any())).thenReturn("4006381333931");
        when(barcodeInfoReturner.getProductInfoFromAllAPIs("4006381333931"))
                .thenReturn("{\"product\":{\"name\":\"Cola\"}}");

        MockMultipartFile file = new MockMultipartFile("file", "barcode.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/v1/products/recognize").file(file))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=info.json"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"product\":{\"name\":\"Cola\"}}"));
    }

    @Test
    void decodeAndGetProductInfo_ProductNotFound_Returns404() throws Exception {
        when(codeRecordService.process(any())).thenReturn("000000");
        when(barcodeInfoReturner.getProductInfoFromAllAPIs("000000"))
                .thenThrow(new ProductNotFoundException("000000"));

        MockMultipartFile file = new MockMultipartFile("file", "barcode.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/api/v1/products/recognize").file(file))
                .andExpect(status().isNotFound());
    }
}