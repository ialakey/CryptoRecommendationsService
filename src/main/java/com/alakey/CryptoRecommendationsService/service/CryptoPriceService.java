package com.alakey.CryptoRecommendationsService.service;

import com.alakey.CryptoRecommendationsService.config.RateLimiterConfig;
import com.alakey.CryptoRecommendationsService.model.CryptoPrice;
import com.alakey.CryptoRecommendationsService.model.CryptoStats;
import com.alakey.CryptoRecommendationsService.repository.CryptoPriceRepository;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoPriceService {

    private final RateLimiterConfig rateLimiterConfig;
    private final CryptoPriceRepository repository;

    private boolean tryConsume() {
        Bucket bucket = rateLimiterConfig.resolveBucket("global");
        return bucket.tryConsume(1);
    }

    public Map<String, Double> getCryptoStats() {
        if (!tryConsume()) {
            throw new RuntimeException("Request limit exceeded");
        }

        List<CryptoPrice> prices = repository.findAll();
        Map<String, Double> result = new HashMap<>();

        Map<String, List<CryptoPrice>> grouped = prices.stream()
                .collect(Collectors.groupingBy(CryptoPrice::getSymbol));

        for (String symbol : grouped.keySet()) {
            List<CryptoPrice> cryptoPrices = grouped.get(symbol);
            double min = cryptoPrices.stream().mapToDouble(CryptoPrice::getPrice).min().orElse(0);
            double max = cryptoPrices.stream().mapToDouble(CryptoPrice::getPrice).max().orElse(0);
            double normalizedRange = (max - min) / min;
            result.put(symbol, normalizedRange);
        }

        return result.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Optional<CryptoPrice> getLatestPrice(String symbol) {
        if (!tryConsume()) {
            throw new RuntimeException("Request limit exceeded");
        }

        return repository.findBySymbol(symbol).stream()
                .max(Comparator.comparing(CryptoPrice::getTimestamp));
    }

    public CryptoStats getCryptoStatsBySymbol(String symbol) {
        if (!tryConsume()) {
            throw new RuntimeException("Request limit exceeded");
        }

        List<CryptoPrice> prices = repository.findBySymbol(symbol);

        if (prices.isEmpty()) {
            throw new IllegalArgumentException("Cryptocurrency " + symbol + " not found");
        }

        double min = prices.stream().mapToDouble(CryptoPrice::getPrice).min().orElse(0);
        double max = prices.stream().mapToDouble(CryptoPrice::getPrice).max().orElse(0);
        double oldest = prices.stream().min(Comparator.comparing(CryptoPrice::getTimestamp)).get().getPrice();
        double newest = prices.stream().max(Comparator.comparing(CryptoPrice::getTimestamp)).get().getPrice();

        return new CryptoStats(symbol, oldest, newest, min, max);
    }

    public Map<String, Double> getCryptoWithHighestRange(String date) {
        if (!tryConsume()) {
            throw new RuntimeException("Request limit exceeded");
        }

        try {
            LocalDate targetDate = LocalDate.parse(date);

            List<CryptoPrice> prices = repository.findAll();

            Map<String, List<CryptoPrice>> grouped = prices.stream()
                    .filter(p -> p.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDate().equals(targetDate))
                    .collect(Collectors.groupingBy(CryptoPrice::getSymbol));

            if (grouped.isEmpty()) {
                return Collections.singletonMap("No data for the specified date: " + date, 0.0);
            }

            String topCrypto = null;
            double highestRange = 0.0;

            for (Map.Entry<String, List<CryptoPrice>> entry : grouped.entrySet()) {
                List<CryptoPrice> dailyPrices = entry.getValue();
                double min = dailyPrices.stream().mapToDouble(CryptoPrice::getPrice).min().orElse(0);
                double max = dailyPrices.stream().mapToDouble(CryptoPrice::getPrice).max().orElse(0);
                double normalizedRange = (max - min) / min;

                if (normalizedRange > highestRange) {
                    highestRange = normalizedRange;
                    topCrypto = entry.getKey();
                }
            }

            return Collections.singletonMap(topCrypto, highestRange);

        } catch (Exception e) {
            throw new RuntimeException("Error processing request: " + e.getMessage());
        }
    }

    public List<CryptoPrice> getHistoricalData(String symbol, int months) {
        if (!tryConsume()) {
            throw new RuntimeException("Request limit exceeded");
        }

        LocalDate targetDate = LocalDate.now().minusMonths(months);
        Instant startTime = targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return repository.findBySymbolAndTimestampAfter(symbol, startTime);
    }
}