package com.example.coderecognizer;

import com.example.coderecognizer.controller.cfg.BarcodeApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties(BarcodeApiProperties.class)
public class CoderecognizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoderecognizerApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
