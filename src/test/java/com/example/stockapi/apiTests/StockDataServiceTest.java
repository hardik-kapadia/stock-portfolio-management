package com.example.stockapi.apiTests;

import com.example.stockapi.dao.StockDataService;
import com.example.stockapi.exception.ApiException;
import com.example.stockapi.model.ETF.StockSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

public class StockDataServiceTest {

    @Mock
    StockDataService stockDataService;

    @BeforeEach
    public void init() {
        this.stockDataService = new StockDataService("U7XCKXTNMNLFSDJP");
    }

    @Test
    public void testSearchApi() throws ApiException {

        List<StockSearchResult> all = this.stockDataService.searchForSymbol("CIPLA");

    }

}
