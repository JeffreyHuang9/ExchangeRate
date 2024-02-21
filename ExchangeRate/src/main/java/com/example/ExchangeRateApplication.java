package com.example;

import com.example.unit.SSLUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExchangeRateApplication {

    public static void main(String[] args) {
        SSLUtils.disableSSLVerification();
        SpringApplication.run(ExchangeRateApplication.class, args);
    }

}
