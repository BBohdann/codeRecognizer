package com.example.coderecognizer.service.dto;

import com.example.coderecognizer.data.entity.ProductCode;
import com.example.coderecognizer.service.utils.ValueType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScanInfoDto {
    private Long id;
    private LocalDateTime scanDateTime;
    private ProductCode productCode;
    private Boolean success;
    private ValueType valueType;
}
