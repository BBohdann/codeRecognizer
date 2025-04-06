package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.data.repository.ProductCodeRepository;
import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.mapper.ProductCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing product code data.
 * Provides functionality to list and save product code entries.
 */
@Service
@RequiredArgsConstructor
public class ProductCodeService {

    private final ProductCodeRepository repository;
    private final ProductCodeMapper mapper;

    /**
     * Retrieves a list of all product codes from the database.
     *
     * @return list of product code DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductCodeDto> listAll() {
        return mapper.toProductCodeDtos(repository.findAll());
    }

    /**
     * Saves a new product code entry to the database.
     *
     * @param dto the product code DTO to be saved
     * @return the saved product code DTO
     */
    @Transactional
    public ProductCodeDto save(ProductCodeDto dto) {
        ProductCode entity = mapper.toProductCodeEntity(dto);
        ProductCode saved = repository.save(entity);
        return mapper.toProductCodeDto(saved);
    }
}
