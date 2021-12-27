package com.example.stockapi.controller;

import com.example.stockapi.dao.StockDataService;
import com.example.stockapi.exception.ApiException;
import com.example.stockapi.model.ETF.StockSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {

    StockDataService stockDataService;

    @Autowired
    public StockController(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }

    @GetMapping("/{stockName}")
    public List<StockSearchResult> searchForStock(@PathVariable String stockName) throws ApiException {
        return this.stockDataService.searchForSymbol(stockName);
    }

}
