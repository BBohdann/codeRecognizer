package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.data.repository.ProductInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service responsible for managing product information records.
 * Allows saving and retrieving product information by barcode.
 */
@Service
@RequiredArgsConstructor
public class ProductInfoService {

    private final ProductInfoRepository repository;

    /**
     * Saves product information with the associated barcode and source.
     *
     * @param codeValue   the barcode value
     * @param productData the product data in JSON format
     * @param source      the data source (API name)
     * @return the saved ProductInfo entity
     */
    @Transactional
    public ProductInfo saveProductInfo(String codeValue, String productData, String source) {
        ProductInfo info = new ProductInfo();
        info.setCodeValue(codeValue);
        info.setProductData(productData);
        info.setSource(source);
        return repository.save(info);
    }

    /**
     * Finds product information by barcode value.
     *
     * @param codeValue the barcode value to search for
     * @return an Optional containing the product info, if found
     */
    @Transactional(readOnly = true)
    public Optional<ProductInfo> findByCodeValue(String codeValue) {
        return repository.findByCodeValue(codeValue);
    }
}
