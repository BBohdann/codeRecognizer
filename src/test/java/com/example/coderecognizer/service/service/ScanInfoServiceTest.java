package com.example.coderecognizer.service.service;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.data.entity.ScanInfo;
import com.example.coderecognizer.data.repository.ProductCodeRepository;
import com.example.coderecognizer.data.repository.ScanInfoRepository;
import com.example.coderecognizer.service.dto.ScanInfoDto;
import com.example.coderecognizer.service.mapper.ScanInfoMapper;
import com.example.coderecognizer.service.service.impl.ScanInfoService;
import com.example.coderecognizer.service.utils.ValueType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScanInfoServiceTest {

    @Mock
    private ScanInfoRepository repository;
    @Mock
    private ProductCodeRepository productCodeRepository;
    @Mock
    private ScanInfoMapper mapper;
    @InjectMocks
    private ScanInfoService service;

    @Test
    void listAll_ReturnsMappedDtoList() {
        ScanInfo entity = new ScanInfo();
        entity.setId(1);
        List<ScanInfo> entities = List.of(entity);

        ScanInfoDto dto = new ScanInfoDto();
        dto.setId(1);
        List<ScanInfoDto> dtos = List.of(dto);

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDtoList(entities)).thenReturn(dtos);

        assertThat(service.listAll()).isEqualTo(dtos);
    }

    @Test
    void save_AttachesLazyProductCodeReferenceAndPersists() {
        ScanInfoDto dto = new ScanInfoDto();
        dto.setProductCodeId(5);
        dto.setSuccess(true);
        dto.setValueType(ValueType.TEXT);
        dto.setScanDateTime(LocalDateTime.now());

        ScanInfo mappedEntity = new ScanInfo();
        ProductCode reference = new ProductCode();
        reference.setId(5);

        ScanInfo savedEntity = new ScanInfo();
        savedEntity.setId(10);

        ScanInfoDto savedDto = new ScanInfoDto();
        savedDto.setId(10);
        savedDto.setProductCodeId(5);

        when(mapper.toEntity(dto)).thenReturn(mappedEntity);
        when(productCodeRepository.getReferenceById(5)).thenReturn(reference);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(savedDto);

        ScanInfoDto result = service.save(dto);

        assertThat(result).isEqualTo(savedDto);
        assertThat(mappedEntity.getProductCode()).isEqualTo(reference);
        verify(repository).save(mappedEntity);
    }
}