package com.example.coderecognizer.service.dto;

import com.example.coderecognizer.service.utils.ValueType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScanInfoDto {
    private Integer id;
    private LocalDateTime scanDateTime;

    @NotNull
    private Integer productCodeId;

    private Boolean success;
    private ValueType valueType;
}
