package com.alakey.CryptoRecommendationsService.controller;

import com.alakey.CryptoRecommendationsService.model.CryptoPrice;
import com.alakey.CryptoRecommendationsService.model.CryptoStats;
import com.alakey.CryptoRecommendationsService.service.CryptoPriceService;
import com.alakey.CryptoRecommendationsService.service.CryptoValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/crypto")
@RequiredArgsConstructor
public class CryptoPriceController {
    private final CryptoPriceService cryptoPriceService;
    private final CryptoValidationService cryptoValidationService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Double>> getCryptoStats() {
        Map<String, Double> stats = cryptoPriceService.getCryptoStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{symbol}/latest")
    public ResponseEntity<?> getLatestPrice(@PathVariable String symbol) {
        if (!cryptoValidationService.isSupported(symbol)) {
            return ResponseEntity.badRequest().body("Unsupported crypto: " + symbol);
        }
        Optional<CryptoPrice> price = cryptoPriceService.getLatestPrice(symbol);
        return price.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{symbol}/stats")
    public ResponseEntity<?> getCryptoStatsBySymbol(@PathVariable String symbol) {
        if (!cryptoValidationService.isSupported(symbol)) {
            return ResponseEntity.badRequest().body("Unsupported crypto: " + symbol);
        }
        CryptoStats stats = cryptoPriceService.getCryptoStatsBySymbol(symbol);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/top")
    public ResponseEntity<?> getCryptoWithHighestRange(@RequestParam String date) {
        if (date == null || date.isBlank()) {
            return ResponseEntity.badRequest().body("Date parameter is required.");
        }
        Map<String, Double> topCrypto = cryptoPriceService.getCryptoWithHighestRange(date);
        return ResponseEntity.ok(topCrypto);
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<?> getHistoricalData(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1") int months) {
        if (!cryptoValidationService.isSupported(symbol)) {
            return ResponseEntity.badRequest().body("Unsupported crypto: " + symbol);
        }
        if (months <= 0 || months > 12) {
            return ResponseEntity.badRequest().body("Invalid months parameter (allowed range: 1-12)");
        }
        List<CryptoPrice> history = cryptoPriceService.getHistoricalData(symbol, months);
        return ResponseEntity.ok(history);
    }
}