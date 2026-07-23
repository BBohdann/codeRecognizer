package com.example.coderecognizer.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
 
@Data
public class ProductCodeDto {
    private Integer id;
 
    @NotBlank
    @Size(max = 150)
    private String codeType;
 
    @NotBlank
    private String codeValue;
 
    @Size(max = 255)
    private String fileName;
}