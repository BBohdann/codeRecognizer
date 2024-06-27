package com.example.coderecognizer.service.dto;

import com.example.coderecognizer.data.entity.ScanInfo;
import com.example.coderecognizer.service.utils.ValueType;
import lombok.Data;

import java.util.List;

@Data
public class ProductCodeDto {
        private Long id;
        private String codeType;
        private String codeValue;
        private String fileName;
//        private ValueType codeValueType;
        private List<ScanInfo> scanInfo;
}
