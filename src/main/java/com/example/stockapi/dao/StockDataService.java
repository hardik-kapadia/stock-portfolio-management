package com.example.stockapi.dao;

import com.example.stockapi.model.ETF.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class StockDataService {

    //TODO: Use API Wrapper instead
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

    public List<Stock> searchForSymbol(String query){
        // TODO: complete search result
        return null;
    }


}
