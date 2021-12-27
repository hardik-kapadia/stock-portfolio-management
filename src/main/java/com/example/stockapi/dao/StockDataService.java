package com.example.stockapi.dao;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.example.stockapi.config.StockApiConfig;
import com.example.stockapi.model.ETF.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class StockDataService {

    StockApiConfig stockApiConfig;

    @Autowired
    public StockDataService(StockApiConfig stockApiConfig) {

        this.stockApiConfig = stockApiConfig;

        AlphaVantage.api().init(Config.builder()
                .key(this.stockApiConfig.API_KEY)
                .timeOut(20)
                .build());
    }

    public Stock getStock(String symbol, String exchange) {
        // TODO: complete with API calls
        return null;
    }

    public void updateStock(Stock stock) {
        // TODO: complete with API calls
    }

    public List<Stock> searchForSymbol(String query) {
        // TODO: complete search result
        return null;
    }

    @Autowired
    public void setStockApiConfig(StockApiConfig stockApiConfig) {
        this.stockApiConfig = stockApiConfig;
    }


}
