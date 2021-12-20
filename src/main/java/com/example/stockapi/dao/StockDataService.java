package com.example.stockapi.dao;

import com.example.stockapi.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StockDataService {

    RestTemplate restTemplate;

    @Autowired
    public StockDataService() {
        this.restTemplate = new RestTemplate();
    }

    public Stock getStock(String symbol, String exchange) {
        // TODO: complete with API calls
        return null;
    }

    public void updateStock(Stock stock){
        // TODO: complete with API calls
    }


}
