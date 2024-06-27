package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.data.entity.ScanInfo;
import com.example.coderecognizer.data.repository.ScanInfoRepository;
import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.mapper.ScanInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScanInfoService {
    private final ScanInfoRepository scanRepository;
    private final ScanInfoMapper scanInfoMapper;

    public List<ScanInfoDto> listAll() {
        return scanInfoMapper.toScanInfoDtos(scanRepository.findAll());
    }

    @Transactional
    public ScanInfoDto save(ScanInfoDto scanInfoDto) {
        ScanInfo entity = scanInfoMapper.toScanInfoEntity(scanInfoDto);
        return scanInfoMapper.toScanInfoDto(scanRepository.save(entity));
    }
}
