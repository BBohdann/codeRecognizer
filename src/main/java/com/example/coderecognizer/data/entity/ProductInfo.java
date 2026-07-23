package com.example.coderecognizer.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "product_info", uniqueConstraints = @UniqueConstraint(columnNames = "code_value"))
public class ProductInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code_value", nullable = false)
    private String codeValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_data", columnDefinition = "jsonb", nullable = false)
    private String productData;

    @Column(name = "source", nullable = false)
    private String source;
}