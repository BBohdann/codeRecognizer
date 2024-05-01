package com.example.coderecognizer.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Entity
@Table(name = "product_codes")
public class ProductCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 5, max = 150)
    private String codeType;

    @NotBlank
    private String codeValue;
}
