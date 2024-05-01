package com.example.coderecognizer.service.dto;

import lombok.Data;

@Data
public class ProductCodeDto {
        private Long id;
        private String codeType;
        private String codeValue;
}
