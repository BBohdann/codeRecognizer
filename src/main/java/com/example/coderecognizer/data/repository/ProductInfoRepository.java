package com.example.coderecognizer.data.repository;

import com.example.coderecognizer.data.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {
    Optional<ProductInfo> findByCodeValue(String codeValue);
}
