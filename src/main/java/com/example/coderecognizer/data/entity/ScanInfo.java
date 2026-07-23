package com.example.coderecognizer.data.entity;

import com.example.coderecognizer.service.utils.ValueType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = "productCode")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "scan_info")
public class ScanInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "scan_datetime", nullable = false)
    private LocalDateTime scanDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code_id", nullable = false)
    private ProductCode productCode;

    @Column(name = "success", nullable = false)
    private Boolean success;

    @Enumerated(EnumType.STRING)
    @Column(name = "code_value_type", nullable = false)
    private ValueType valueType;
}
