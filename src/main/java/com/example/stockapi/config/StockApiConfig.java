package com.example.stockapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:secret.data")
public class StockApiConfig {

    public final String API_KEY;

    public StockApiConfig(@Value("${API_KEY}") String API_KEY) {
        this.API_KEY = API_KEY;
    }

}
