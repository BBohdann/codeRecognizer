package com.example.coderecognizer.data.repository;

import com.example.coderecognizer.data.entity.ProductCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCodeRepository extends JpaRepository<ProductCode, Long> {
}
