package com.example.coderecognizer.data.repository;

import com.example.coderecognizer.data.entity.ScanInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScanInfoRepository extends JpaRepository<ScanInfo , Long> {
}
