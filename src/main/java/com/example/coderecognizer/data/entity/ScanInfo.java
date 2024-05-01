package com.example.coderecognizer.data.entity;

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
    @JoinColumn(name = "product_code_id", nullable = false)
    private ProductCode productCode;

    @Column(name = "success" )
    private Boolean success;
}
