package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.data.repository.ProductInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductInfoService {
    private final ProductInfoRepository productInfoRepository;

    @Transactional
    public ProductInfo saveProductInfo(String codeValue, String productData, String source) {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setCodeValue(codeValue);
        productInfo.setProductData(productData);
        productInfo.setSource(source);

        return productInfoRepository.save(productInfo);
    }

    @Transactional(readOnly = true)
    public Optional<ProductInfo> findByCodeValue(String codeValue) {
        return productInfoRepository.findByCodeValue(codeValue);
    }
}
