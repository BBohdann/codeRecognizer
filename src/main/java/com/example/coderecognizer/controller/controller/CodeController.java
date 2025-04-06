package com.example.coderecognizer.controller.controller;

import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.service.CodeRecordService;
import com.example.coderecognizer.service.service.barcode.BarcodeInfoReturner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller responsible for handling barcode recognition and product information retrieval.
 * It supports decoding barcodes from image files, retrieving product details from third-party APIs,
 * and returning downloadable JSON data based on scanned codes.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/recognizer")
public class CodeController {

    private final CodeRecordService codeRecordService;
    private final BarcodeInfoReturner barcodeInfoReturner;

    /**
     * Decodes a barcode from the provided image file.
     * This endpoint accepts a multipart image file, processes it to extract the barcode string,
     * and returns the decoded code in plain text.
     *
     * @param file the image file containing the barcode
     * @return the decoded barcode string
     * @throws InvalidImageFormatException if the image format is unsupported or invalid
     * @throws IOException                 if an I/O error occurs while reading the file
     * @throws EmptyImageException         if the uploaded image is empty or unreadable
     */
    @GetMapping("/decode")
    public ResponseEntity<String> getFile(@RequestPart("file") MultipartFile file)
            throws InvalidImageFormatException, IOException, EmptyImageException {
        String result = codeRecordService.process(file);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves product information from external APIs based on the barcode string.
     * This endpoint takes a raw barcode value as a query parameter and returns the corresponding
     * product information in JSON format, aggregated from multiple open food APIs.
     *
     * @param codeInfo the barcode string to search for
     * @return JSON-formatted product information
     */
    @GetMapping("/searchProduct")
    public ResponseEntity<String> checkProductOnOpenFood(@RequestParam("codeInfo") String codeInfo) {
        String jsonInfo = barcodeInfoReturner.getProductInfoFromAllAPIs(codeInfo);
        return ResponseEntity.ok(jsonInfo);
    }

    /**
     * Decodes a barcode and returns product information as a downloadable JSON file.
     * This combined operation processes the image to extract the barcode and then fetches
     * detailed product information from external sources. The response includes headers
     * to trigger a file download with the content named "info.json".
     *
     * @param file the image file containing the barcode
     * @return ResponseEntity containing the product info JSON and download headers
     * @throws InvalidImageFormatException if the image format is unsupported or invalid
     * @throws IOException                 if an I/O error occurs while reading the file
     * @throws EmptyImageException         if the uploaded image is empty or unreadable
     */
    @GetMapping("/decodeAndSearch")
    public ResponseEntity<String> decodeAndGetProductInfo(@RequestPart("file") MultipartFile file)
            throws InvalidImageFormatException, IOException, EmptyImageException {
        String result = codeRecordService.process(file);
        String json = barcodeInfoReturner.getProductInfoFromAllAPIs(result);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=info.json");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}