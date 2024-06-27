package com.example.coderecognizer.service.service.barcode;

import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.service.service.impl.ProductInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BarcodeInfoReturner {
    private final ProductInfoService productInfoService;
    private static final Map<String, String> API_URLS = new HashMap<>();

    static {
        API_URLS.put("OpenFoodFacts", "https://world.openfoodfacts.org/api/v0/product/%s.json");
        API_URLS.put("Product Open Data", "https://pod.opendatasoft.com/api/records/1.0/search/?dataset=opendatasoft-barcodes&q=%s");
        API_URLS.put("UPCItemDB", "https://api.upcitemdb.com/prod/trial/lookup?upc=%s");
        API_URLS.put("Datakick", "https://www.gtinsearch.org/api/items/%s");
        API_URLS.put("Open Beauty Facts", "https://world.openbeautyfacts.org/api/v0/product/%s");
    }
    private String getProductInfo(String Url) {
        try {
            URL url = new URL(Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 400)
                return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            if (containsNotFoundMessage(response.toString()))
                return null;
            reader.close();
            connection.disconnect();
            return response.toString();
        }catch (Exception e) {
            return null;
        }
    }

    public String getProductInfoFromAllAPIs(String barcode) {
        Optional<ProductInfo> productInfoOptional = productInfoService.findByCodeValue(barcode);
        if (productInfoOptional.isPresent())
            return productInfoOptional.get().getProductData();
        for (Map.Entry<String, String> entry : API_URLS.entrySet()) {
            String apiUrl = String.format(entry.getValue(), barcode);
            String response = getProductInfo(apiUrl);
            if (response != null && !response.isEmpty()) {
                productInfoService.saveProductInfo(barcode, response, entry.getKey());
                return response;
            }
        }
        throw new RuntimeException("Product not found on any API");
    }
    private boolean containsNotFoundMessage(String response) {
        String notFoundMessage1 = "\"status\":0,\"status_verbose\":\"product not found\"";
        String notFoundMessage2 = "status\":0";
        return response.contains(notFoundMessage1) || response.contains(notFoundMessage2);
    }
}