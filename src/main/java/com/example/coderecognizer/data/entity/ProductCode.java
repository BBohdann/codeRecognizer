package com.example.coderecognizer.data.entity;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString(exclude = "scanInfo")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "product_codes")
public class ProductCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false)
    private String codeType;

    @NotBlank
    @Column(nullable = false)
    private String codeValue;

    @OneToMany(mappedBy = "productCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScanInfo> scanInfo = new ArrayList<>();

    @Size(max = 255)
    @Column
    private String fileName;
}
