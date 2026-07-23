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

    private final ProductCodeRepository repository;
    private final ProductCodeMapper mapper;

    @Transactional(readOnly = true)
    public List<ProductCodeDto> listAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional
    public ProductCodeDto save(ProductCodeDto dto) {
        ProductCode entity = mapper.toEntity(dto);
        ProductCode saved = repository.save(entity);
        return mapper.toDto(saved);
    }
}
