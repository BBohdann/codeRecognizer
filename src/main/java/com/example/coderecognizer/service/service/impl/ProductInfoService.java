package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.data.repository.ProductInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductInfoService {
    private final ProductInfoRepository repository;

    @Transactional
    public ProductInfo saveProductInfo(String codeValue, String productData, String source) {
        ProductInfo info = new ProductInfo();
        info.setCodeValue(codeValue);
        info.setProductData(productData);
        info.setSource(source);
        return repository.save(info);
    }

    @Transactional(readOnly = true)
    public Optional<ProductInfo> findByCodeValue(String codeValue) {
        return repository.findByCodeValue(codeValue);
    }
}
