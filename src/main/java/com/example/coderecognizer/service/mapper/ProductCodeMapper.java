package com.example.coderecognizer.service.mapper;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.service.dto.ProductCodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductCodeMapper {

    ProductCodeDto toDto(ProductCode entity);

    @Mapping(target = "scanInfo", ignore = true)
    ProductCode toEntity(ProductCodeDto dto);

    List<ProductCodeDto> toDtoList(Collection<ProductCode> entities);

    List<ProductCode> toEntityList(Collection<ProductCodeDto> dtos);
}
