package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.data.entity.ScanInfo;
import com.example.coderecognizer.data.repository.ProductCodeRepository;
import com.example.coderecognizer.data.repository.ScanInfoRepository;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.mapper.ScanInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScanInfoService {
    private final ScanInfoRepository repository;
    private final ProductCodeRepository productCodeRepository;
    private final ScanInfoMapper mapper;

    @Transactional(readOnly = true)
    public List<ScanInfoDto> listAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Transactional
    public ScanInfoDto save(ScanInfoDto dto) {
        ScanInfo entity = mapper.toEntity(dto);
        ProductCode productCodeRef = productCodeRepository.getReferenceById(dto.getProductCodeId());
        entity.setProductCode(productCodeRef);

        ScanInfo saved = repository.save(entity);
        return mapper.toDto(saved);
    }
}