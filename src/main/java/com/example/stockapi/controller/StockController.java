package com.example.stockapi.controller;

import com.example.stockapi.dao.StockDataService;
import com.example.stockapi.exception.ApiException;
import com.example.stockapi.model.ETF.Stock;
import com.example.stockapi.model.ETF.StockSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/stocks")
public class StockController {

    // Used to fetch Data from the API and then convert it to a preferred format
    StockDataService stockDataService;

    @Autowired
    public StockController(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }

    @GetMapping("/search/{stockName}")
    public ResponseEntity<List<StockSearchResult>> searchForStock(@PathVariable String stockName) throws ApiException {
        return new ResponseEntity<>(this.stockDataService.searchForSymbol(stockName), HttpStatus.OK);
    }

    @GetMapping("/stock/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) throws ApiException {
        return new ResponseEntity<>(this.stockDataService.getStockFromSymbol(symbol), HttpStatus.OK);
    }

}
