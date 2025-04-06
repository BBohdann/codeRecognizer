package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.data.entity.ScanInfo;
import com.example.coderecognizer.data.repository.ScanInfoRepository;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.mapper.ScanInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for handling scan information entries.
 * Supports listing all scan records and saving new ones.
 */
@Service
@RequiredArgsConstructor
public class ScanInfoService {

    private final ScanInfoRepository repository;
    private final ScanInfoMapper mapper;

    /**
     * Retrieves all scan information entries from the database.
     *
     * @return list of scan info DTOs
     */
    @Transactional(readOnly = true)
    public List<ScanInfoDto> listAll() {
        return mapper.toScanInfoDtos(repository.findAll());
    }

    /**
     * Saves a new scan information record to the database.
     *
     * @param dto the scan info DTO to save
     * @return the saved scan info DTO
     */
    @Transactional
    public ScanInfoDto save(ScanInfoDto dto) {
        ScanInfo entity = mapper.toScanInfoEntity(dto);
        ScanInfo saved = repository.save(entity);
        return mapper.toScanInfoDto(saved);
    }
}