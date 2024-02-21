package com.example.service;

import com.example.model.ExchangeRate;

import java.util.List;

public interface ExchangeRateService {
    String fetchDataAndSaveToDatabase();
    List<ExchangeRate> getAllExchangeRates();
    void addExchangeRate(ExchangeRate exchangeRate);
    void updateExchangeRate(Long id, ExchangeRate exchangeRate);
    ExchangeRate getExchangeRate(Long id);
    void deleteExchangeRate(Long id);
}
