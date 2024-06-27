package com.example.coderecognizer.data.entity;

import com.example.coderecognizer.service.utils.ValueType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table
public class ScanInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scan_datetime", nullable = false)
    private LocalDateTime scanDateTime;

    @ManyToOne
    @JoinColumn(name = "product_code_id")
    private ProductCode productCode;

    @Column(name = "success")
    private Boolean success;

    @Enumerated(EnumType.STRING)
    @Column(name = "code_value_type", nullable = false)
    private ValueType valueType;
}
