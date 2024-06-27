package com.example.coderecognizer.service.mapper;

import com.example.coderecognizer.data.entity.ScanInfo;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ScanInfoMapper {

    public List<ScanInfo> toScanInfoEntities(Collection<ScanInfoDto> dtos) {
        return dtos.stream()
                .map(this::toScanInfoEntity)
                .toList();
    }

    public ScanInfo toScanInfoEntity(ScanInfoDto dto) {
        ScanInfo entity = new ScanInfo();
        entity.setId(dto.getId());
        entity.setProductCode(dto.getProductCode());
        entity.setScanDateTime(dto.getScanDateTime());
        entity.setSuccess(dto.getSuccess());
        entity.setValueType(dto.getValueType());
        return entity;
    }

    public List<ScanInfoDto> toScanInfoDtos(Collection<ScanInfo> entities) {
        return entities.stream()
                .map(this::toScanInfoDto).toList();
    }

    public ScanInfoDto toScanInfoDto(ScanInfo entity) {
        ScanInfoDto dto = new ScanInfoDto();
        dto.setId(entity.getId());
        dto.setProductCode(entity.getProductCode());
        dto.setScanDateTime(entity.getScanDateTime());
        dto.setValueType(entity.getValueType());
        dto.setSuccess(entity.getSuccess());
        return dto;
    }
}
