package com.example.coderecognizer.controller.controller;

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


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/recognizer")
public class CodeController {
    private final CodeRecordService codeRecordService;
    private final BarcodeInfoReturner barcodeInfoReturner;

    @GetMapping("/decode")
    public ResponseEntity<String> getFile(@RequestPart("file") MultipartFile file) throws InvalidImageFormatException {
        String result = codeRecordService.proccess(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/searchProduct")
    public ResponseEntity<String> checkProductOnOpenFood(@RequestParam("codeInfo") String codeInfo) {
        String jsonInfo = barcodeInfoReturner.getProductInfoFromAllAPIs(codeInfo);
        return ResponseEntity.ok(jsonInfo);
    }

    @GetMapping("/decodeAndSearch")
    public ResponseEntity<String> decodeWith(@RequestPart("file") MultipartFile file) throws InvalidImageFormatException {
        String result = codeRecordService.proccess(file);
        String json = barcodeInfoReturner.getProductInfoFromAllAPIs(result);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=info.json");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}

