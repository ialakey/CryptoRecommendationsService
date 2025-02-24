package com.alakey.CryptoRecommendationsService.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CryptoStats {
    private String symbol;
    private double oldest;
    private double newest;
    private double min;
    private double max;
}