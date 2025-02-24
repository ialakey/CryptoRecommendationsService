package com.alakey.CryptoRecommendationsService.repository;

import com.alakey.CryptoRecommendationsService.model.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {
    List<CryptoPrice> findBySymbol(String symbol);

    List<CryptoPrice> findBySymbolAndTimestampAfter(String symbol, Instant timestamp);
}