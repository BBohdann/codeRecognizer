package com.example.coderecognizer.data.entity;

import com.example.coderecognizer.service.utils.ValueType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Entity
@Table(name = "product_codes")
public class ProductCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 5, max = 150)
    @Column
    private String codeType;

    @NotBlank
    @Column
    private String codeValue;

    @OneToMany(mappedBy = "productCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScanInfo> scanInfo;

    @Length(min = 2, max = 255)
    @Column
    private String fileName;
}
