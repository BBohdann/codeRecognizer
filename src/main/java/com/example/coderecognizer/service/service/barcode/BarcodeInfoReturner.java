package com.example.coderecognizer.service.service.barcode;

import com.example.coderecognizer.controller.cfg.BarcodeApiProperties;
import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.service.exeption.ProductNotFoundException;
import com.example.coderecognizer.service.service.impl.ProductInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BarcodeInfoReturner {
    private static final String NOT_FOUND_MARKER = "\"status\":0";

    private final ProductInfoService productInfoService;
    private final RestTemplate restTemplate;
    private final BarcodeApiProperties apiProperties;

    public String getProductInfoFromAllAPIs(String barcode) {
        return productInfoService.findByCodeValue(barcode)
                .map(ProductInfo::getProductData)
                .orElseGet(() -> fetchFromExternalApis(barcode));
    }

    private String fetchFromExternalApis(String barcode) {
        Map<String, String> apis = apiProperties.getApis();
        if (apis.isEmpty()) {
            log.warn("No external product APIs are configured (barcode.apis is empty) - no request will be sent for code {}", barcode);
        }

        for (Map.Entry<String, String> entry : apis.entrySet()) {
            String apiName = entry.getKey();
            String url = String.format(entry.getValue(), barcode);

            Optional<String> response = fetchProductInfo(url);
            if (response.isPresent()) {
                productInfoService.saveProductInfo(barcode, response.get(), apiName);
                return response.get();
            }
        }
        throw new ProductNotFoundException(barcode);
    }

    Optional<String> fetchProductInfo(String url) {
        try {
            String body = restTemplate.getForObject(url, String.class);
            if (body == null || body.isBlank() || containsNotFoundMessage(body)) {
                return Optional.empty();
            }
            return Optional.of(body);
        } catch (HttpClientErrorException.BadRequest e) {
            log.debug("Provider rejected the request for {}: {}", url, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Could not reach product API at {}: {}", url, e.getMessage());
            return Optional.empty();
        }
    }

    private boolean containsNotFoundMessage(String response) {
        return response.contains(NOT_FOUND_MARKER);
    }
}