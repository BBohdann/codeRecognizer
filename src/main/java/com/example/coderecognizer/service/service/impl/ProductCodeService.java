package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.data.repository.ProductCodeRepository;
import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.mapper.ProductCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCodeService {
    private final ProductCodeRepository productRepository;
    private final ProductCodeMapper productMapper;

    public List<ProductCodeDto> listAll() {
        return productMapper.toProductCodeDtos(productRepository.findAll());
    }

    @Transactional
    public ProductCodeDto save(ProductCodeDto product) {
        ProductCode entity = productMapper.toProductCodeEntity(product);
        return productMapper.toProductCodeDto(productRepository.save(entity));
    }
}
