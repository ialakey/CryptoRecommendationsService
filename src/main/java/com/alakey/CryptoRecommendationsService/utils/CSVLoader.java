package com.alakey.CryptoRecommendationsService.utils;

import com.alakey.CryptoRecommendationsService.model.CryptoPrice;
import com.alakey.CryptoRecommendationsService.repository.CryptoPriceRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CSVLoader {
    private final CryptoPriceRepository repository;
    private static final String DATA_FOLDER = "data/";
    private static final String DATA_FOLDER_DOCKER = "/app/data/";

    @PostConstruct
    public void loadCsvData() {
        try {
            File folder;
            if (isRunningInDocker()) {
                folder = new File(DATA_FOLDER_DOCKER);
            } else {
                folder = new ClassPathResource(DATA_FOLDER).getFile();
            }

            File[] csvFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

            if (csvFiles == null || csvFiles.length == 0) {
                log.warn("There are no CSV files to download");
                return;
            }

            log.info("{} files found for download", csvFiles.length);
            for (File file : csvFiles) {
                processFile(file);
            }
        } catch (Exception e) {
            log.error("Error accessing data folder: {}", e.getMessage());
        }
    }

    private boolean isRunningInDocker() {
        return new File("/app").exists();
    }

    private void processFile(File file) {
        log.info("Processing the file: {}", file.getName());

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] headers = reader.readNext();
            log.info("Missing title line: {}", (Object) headers);

            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                if (line.length < 3) {
                    log.warn("Error in line {}: not enough data", (Object) line);
                    continue;
                }

                try {
                    CryptoPrice price = new CryptoPrice();
                    price.setTimestamp(Instant.ofEpochMilli(Long.parseLong(line[0])));
                    price.setSymbol(line[1]);
                    price.setPrice(Double.parseDouble(line[2]));
                    repository.save(price);
                } catch (Exception e) {
                    log.error("Error processing string {}: {}", (Object) line, e.getMessage());
                }
            }
            log.info("File {} uploaded successfully", file.getName());
        } catch (IOException | CsvException e) {
            log.error("Error loading file {}: {}", file.getName(), e.getMessage());
        }
    }
}
