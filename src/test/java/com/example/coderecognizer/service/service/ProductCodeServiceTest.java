package com.example.coderecognizer.service.service;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.data.repository.ProductCodeRepository;
import com.example.coderecognizer.service.dto.ProductCodeDto;
import com.example.coderecognizer.service.mapper.ProductCodeMapper;
import com.example.coderecognizer.service.service.impl.ProductCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCodeServiceTest {

    @Mock
    private ProductCodeRepository repository;
    @Mock
    private ProductCodeMapper mapper;
    @InjectMocks
    private ProductCodeService service;

    @Test
    void listAll_ReturnsMappedDtoList() {
        ProductCode entity = new ProductCode();
        entity.setId(1);
        entity.setCodeType("QR Code");
        entity.setCodeValue("123");
        List<ProductCode> entities = List.of(entity);

        ProductCodeDto dto = new ProductCodeDto();
        dto.setId(1);
        dto.setCodeType("QR Code");
        dto.setCodeValue("123");
        List<ProductCodeDto> dtos = List.of(dto);

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDtoList(entities)).thenReturn(dtos);

        assertThat(service.listAll()).isEqualTo(dtos);
    }

    @Test
    void save_MapsEntityAndPersistsIt_ReturnsMappedDto() {
        ProductCodeDto inputDto = new ProductCodeDto();
        inputDto.setCodeType("QR Code");
        inputDto.setCodeValue("123");

        ProductCode mappedEntity = new ProductCode();
        mappedEntity.setCodeType("QR Code");
        mappedEntity.setCodeValue("123");

        ProductCode savedEntity = new ProductCode();
        savedEntity.setId(1);
        savedEntity.setCodeType("QR Code");
        savedEntity.setCodeValue("123");

        ProductCodeDto savedDto = new ProductCodeDto();
        savedDto.setId(1);
        savedDto.setCodeType("QR Code");
        savedDto.setCodeValue("123");

        when(mapper.toEntity(inputDto)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(savedDto);

        ProductCodeDto result = service.save(inputDto);

        assertThat(result).isEqualTo(savedDto);
        verify(repository).save(mappedEntity);
    }
}