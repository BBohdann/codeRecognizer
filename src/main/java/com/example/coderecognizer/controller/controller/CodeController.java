package com.example.coderecognizer.controller.controller;

import com.example.coderecognizer.controller.ErrorResponse;
import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.service.CodeRecordService;
import com.example.coderecognizer.service.service.barcode.BarcodeInfoReturner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Barcode Recognition", description = "Recognizing barcodes/QR codes and looking up product information")
public class CodeController {
    private final CodeRecordService codeRecordService;
    private final BarcodeInfoReturner barcodeInfoReturner;

    @Operation(
            summary = "Recognize a code from an image",
            description = "Accepts an image containing a barcode or QR code, decodes it using ZXing, "
                    + "and persists the result to the database. Supported image formats: JPEG, PNG, GIF."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Code successfully recognized",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string", example = "4006381333931"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "File is missing, empty, or has an unsupported image format",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Image is valid but no barcode/QR code could be found in it",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping(value = "/codes/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> decode(
            @Parameter(
                    description = "Image containing a barcode/QR code (JPEG, PNG, or GIF)",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            @RequestPart("file") MultipartFile file)
            throws InvalidImageFormatException, IOException, EmptyImageException {
        String result = codeRecordService.process(file);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get product information by code",
            description = "Looks up product information for a barcode value: checks the local cache first, "
                    + "then queries the external APIs (e.g. Open Food Facts) configured in application.yml, in order."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product information found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "string", example = "{\"product\":{\"name\":\"Cola\"}}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The codeInfo parameter is missing or blank",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "None of the external APIs have information for this code",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @GetMapping("/products")
    public ResponseEntity<String> checkProductOnOpenFood(
            @Parameter(description = "Barcode/QR code value of the product", required = true, example = "4006381333931")
            @RequestParam("codeInfo") @NotBlank String codeInfo) {
        String jsonInfo = barcodeInfoReturner.getProductInfoFromAllAPIs(codeInfo);
        return ResponseEntity.ok(jsonInfo);
    }

    @Operation(
            summary = "Recognize a code and download product information",
            description = "Combines image recognition and product lookup in a single request: decodes the code "
                    + "from the image, retrieves the product information, and returns it as a downloadable JSON file "
                    + "(Content-Disposition: attachment; filename=info.json)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product information found and returned as a JSON file",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "string", example = "{\"product\":{\"name\":\"Cola\"}}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "File is missing, empty, or has an unsupported image format",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Code was recognized, but no product information was found in any API",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Image is valid but no barcode/QR code could be found in it",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @PostMapping(value = "/products/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> decodeAndGetProductInfo(
            @Parameter(
                    description = "Image containing a barcode/QR code (JPEG, PNG, or GIF)",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            @RequestPart("file") MultipartFile file)
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