package com.example.coderecognizer.service.mapper;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.service.dto.ProductCodeDto;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;

@Component
public class ProductCodeMapper {
    public List<ProductCode> toProductCodeEntities(Collection<ProductCodeDto> dtos) {
        return dtos.stream()
                .map(this::toProductCodeEntity)
                .toList();
    }

    public ProductCode toProductCodeEntity(ProductCodeDto dto) {
        ProductCode entity = new ProductCode();
        entity.setId(dto.getId());
        entity.setCodeType(dto.getCodeType());
        entity.setCodeValue(dto.getCodeValue());
        entity.setFileName(dto.getFileName());
        return entity;
    }

    public List<ProductCodeDto> toProductCodeDtos(Collection<ProductCode> entities) {
        return entities.stream()
                .map(this::toProductCodeDto).toList();
    }

    public ProductCodeDto toProductCodeDto(ProductCode entity) {
        ProductCodeDto dto = new ProductCodeDto();
        dto.setId(entity.getId());
        dto.setCodeType(entity.getCodeType());
        dto.setCodeValue(entity.getCodeValue());
        return dto;
    }
}
