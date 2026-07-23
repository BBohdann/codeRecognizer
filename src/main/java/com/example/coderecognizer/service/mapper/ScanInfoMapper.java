package com.example.coderecognizer.service.mapper;

import com.example.coderecognizer.data.entity.ScanInfo;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScanInfoMapper {

    @Mapping(target = "productCodeId", source = "productCode.id")
    ScanInfoDto toDto(ScanInfo entity);

    @Mapping(target = "productCode", ignore = true)
    ScanInfo toEntity(ScanInfoDto dto);

    List<ScanInfoDto> toDtoList(Collection<ScanInfo> entities);
}