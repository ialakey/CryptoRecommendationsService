package com.alakey.CryptoRecommendationsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Crypto Recommendations API", version = "1.0", description = "Cryptocurrency recommendation service"))
public class CryptoRecommendationsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CryptoRecommendationsServiceApplication.class, args);
	}
}