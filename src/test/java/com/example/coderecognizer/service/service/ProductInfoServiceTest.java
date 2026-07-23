package com.example.coderecognizer.service.service;

import com.example.coderecognizer.data.entity.ProductInfo;
import com.example.coderecognizer.data.repository.ProductInfoRepository;
import com.example.coderecognizer.service.service.impl.ProductInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductInfoServiceTest {
    @Mock
    private ProductInfoRepository repository;
    @InjectMocks
    private ProductInfoService service;

    @Test
    void saveProductInfo_CreatesEntityAndDelegatesToRepository() {
        ProductInfo saved = new ProductInfo();
        saved.setId(1);
        saved.setCodeValue("123");
        saved.setProductData("{\"name\":\"Cola\"}");
        saved.setSource("api1");
        when(repository.save(any(ProductInfo.class))).thenReturn(saved);

        ProductInfo result = service.saveProductInfo("123", "{\"name\":\"Cola\"}", "api1");

        assertThat(result).isEqualTo(saved);

        ArgumentCaptor<ProductInfo> captor = ArgumentCaptor.forClass(ProductInfo.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCodeValue()).isEqualTo("123");
        assertThat(captor.getValue().getProductData()).isEqualTo("{\"name\":\"Cola\"}");
        assertThat(captor.getValue().getSource()).isEqualTo("api1");
    }

    @Test
    void findByCodeValue_DelegatesToRepository() {
        ProductInfo found = new ProductInfo();
        found.setCodeValue("123");
        when(repository.findByCodeValue("123")).thenReturn(Optional.of(found));

        assertThat(service.findByCodeValue("123")).contains(found);
    }

    @Test
    void findByCodeValue_NotFound_ReturnsEmptyOptional() {
        when(repository.findByCodeValue("999")).thenReturn(Optional.empty());

        assertThat(service.findByCodeValue("999")).isEmpty();
    }
}