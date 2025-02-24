package com.alakey.CryptoRecommendationsService.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class CryptoValidationService {
    private static final String DATA_FOLDER = "data/";
    private final Set<String> supportedCryptos = new HashSet<>();

    @PostConstruct
    public void loadSupportedCryptos() {
        try {
            File folder;
            if (isRunningInDocker()) {
                folder = new File("/app/data/");
            } else {
                folder = new ClassPathResource(DATA_FOLDER).getFile();
            }

            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

            if (files == null || files.length == 0) {
                log.warn("There are no cryptocurrency files in folder {}", DATA_FOLDER);
                return;
            }

            for (File file : files) {
                String cryptoSymbol = file.getName()
                        .replace(".csv", "")
                        .replace("_values", "")
                        .toUpperCase();
                supportedCryptos.add(cryptoSymbol);
            }

            log.info("Loaded allowed cryptocurrencies: {}", supportedCryptos);
        } catch (IOException e) {
            log.error("Error accessing folder {}: {}", DATA_FOLDER, e.getMessage());
        }
    }

    private boolean isRunningInDocker() {
        return new File("/app").exists();
    }

    public boolean isSupported(String cryptoSymbol) {
        return supportedCryptos.contains(cryptoSymbol.toUpperCase());
    }
}