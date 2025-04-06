package com.example.coderecognizer.service.service.barcode;

import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.service.service.impl.ProductInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Component responsible for retrieving product information by barcode.
 * It first checks the local database for cached product data.
 * If not found, it queries multiple public APIs until one returns a valid response.
 */
@Component
@RequiredArgsConstructor
public class BarcodeInfoReturner {

    private final ProductInfoService productInfoService;

    private static final Map<String, String> API_URLS = Map.of(
            "OpenFoodFacts", "https://world.openfoodfacts.org/api/v0/product/%s.json",
            "Product Open Data", "https://pod.opendatasoft.com/api/records/1.0/search/?dataset=opendatasoft-barcodes&q=%s",
            "UPCItemDB", "https://api.upcitemdb.com/prod/trial/lookup?upc=%s",
            "Datakick", "https://www.gtinsearch.org/api/items/%s",
            "Open Beauty Facts", "https://world.openbeautyfacts.org/api/v0/product/%s"
    );

    private static final int BAD_REQUEST_CODE = 400;
    private static final List<String> NOT_FOUND_MESSAGES = List.of(
            "\"status\":0,\"status_verbose\":\"product not found\"",
            "status\":0"
    );

    /**
     * Returns product information associated with the given barcode.
     * First attempts to retrieve the data from the local database. If not found,
     * it sequentially queries the external APIs until valid product information is retrieved.
     *
     * @param barcode the barcode value to search for
     * @return the product information in JSON format
     * @throws RuntimeException if the product is not found in any of the external APIs
     */
    public String getProductInfoFromAllAPIs(String barcode) {
        return productInfoService.findByCodeValue(barcode)
                .map(ProductInfo::getProductData)
                .orElseGet(() -> fetchFromExternalApis(barcode));
    }

    /**
     * Tries to fetch product information from a list of external APIs in order.
     * Stops as soon as a non-empty and valid response is returned.
     *
     * @param barcode the barcode value to look up
     * @return product data as JSON string
     * @throws RuntimeException if none of the APIs return valid product info
     */
    private String fetchFromExternalApis(String barcode) {
        for (Map.Entry<String, String> entry : API_URLS.entrySet()) {
            String apiName = entry.getKey();
            String formattedUrl = String.format(entry.getValue(), barcode);
            String response = fetchProductInfo(formattedUrl);
            if (response != null && !response.isEmpty()) {
                productInfoService.saveProductInfo(barcode, response, apiName);
                return response;
            }
        }
        throw new RuntimeException("Product not found on any API");
    }

    /**
     * Makes a GET request to the specified URL and returns the response body as a string.
     * Skips responses indicating "bad request" or "not found" messages.
     *
     * @param urlString the full URL to request
     * @return the response body or null if invalid
     */
    String fetchProductInfo(String urlString) {
        try {
            HttpURLConnection connection = openConnection(urlString);
            if (connection.getResponseCode() == BAD_REQUEST_CODE) {
                return null;
            }

            String response = readResponse(connection);
            if (containsNotFoundMessage(response)) {
                return null;
            }

            return response;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Opens an HTTP connection to the given URL string.
     *
     * @param urlString the URL string to connect to
     * @return an open HttpURLConnection
     * @throws IOException if the connection fails
     */
    public HttpURLConnection openConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * Reads the full response body from an open HTTP connection.
     *
     * @param connection the HTTP connection to read from
     * @return the response as a string
     * @throws IOException if an error occurs during reading
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            return responseBuilder.toString();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Checks whether the given response contains known "not found" indicators.
     *
     * @param response the response string to check
     * @return true if the product is not found, false otherwise
     */
    private boolean containsNotFoundMessage(String response) {
        return NOT_FOUND_MESSAGES.stream().anyMatch(response::contains);
    }
}
