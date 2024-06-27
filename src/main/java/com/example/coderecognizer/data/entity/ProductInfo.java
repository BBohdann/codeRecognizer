package com.example.coderecognizer.data.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "product_info")
public class ProductInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_value", nullable = false)
    private String codeValue;

    @Column(name = "product_data", columnDefinition = "jsonb", nullable = false)
    private String productData;

    @Column(name = "source", nullable = false)
    private String source;
}