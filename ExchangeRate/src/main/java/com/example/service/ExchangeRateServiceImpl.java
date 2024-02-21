package com.example.service;

import com.example.model.ExchangeRate;
import com.example.repository.ExchangeRateRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    private Gson gson = new Gson();

    @PostConstruct
    public void init() {
        fetchDataAndSaveToDatabase();
    }

    @Override
    public String fetchDataAndSaveToDatabase() {
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL("https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("If-Modified-Since", "Mon, 26 Jul 1997 05:00:00 GMT");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Pragma", "no-cache");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Response: " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 清除数据库旧数据
        exchangeRateRepository.deleteAll();

        List<Map<String, String>> sourceExchangeRateList = gson.fromJson(response.toString(), new TypeToken<List<Map<String, String>>>() {}.getType());
        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        for(Map<String, String> objectMap : sourceExchangeRateList){
            ExchangeRate exchangeRate = new ExchangeRate();
            if(objectMap.containsKey("Date")) exchangeRate.setDate(objectMap.get("Date"));
            if(objectMap.containsKey("USD/NTD")) exchangeRate.setUsdToNtd(objectMap.get("USD/NTD"));
            if(objectMap.containsKey("RMB/NTD")) exchangeRate.setRmbToNtd(objectMap.get("RMB/NTD"));
            if(objectMap.containsKey("USD/RMB")) exchangeRate.setUsdToRmb(objectMap.get("USD/RMB"));
            exchangeRateList.add(exchangeRate);
        }

        if(!ObjectUtils.isEmpty(exchangeRateList) && exchangeRateList.size() > 0){
            exchangeRateRepository.saveAll(exchangeRateList);
        }

        return response.toString();
    }

    @Override
    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    @Override
    public void addExchangeRate(ExchangeRate exchangeRate) {
        exchangeRateRepository.save(exchangeRate);
    }

    @Override
    public void updateExchangeRate(Long id, ExchangeRate exchangeRate) {
        Optional<ExchangeRate> existingExchangeRate = exchangeRateRepository.findById(id);
        if (existingExchangeRate.isPresent()) {
            exchangeRate.setId(id);
            exchangeRateRepository.save(exchangeRate);
        }
    }

    @Override
    public ExchangeRate getExchangeRate(Long id) {
        return exchangeRateRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteExchangeRate(Long id) {
        exchangeRateRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 * * *") // 每天整点执行一次
    public void scheduledFetchDataAndSaveToDatabase() {
        fetchDataAndSaveToDatabase();
    }
}
