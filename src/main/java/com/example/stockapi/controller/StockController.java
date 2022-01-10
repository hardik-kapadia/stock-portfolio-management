package com.example.stockapi.controller;

import com.example.stockapi.repository.StockDataRepo;
import com.example.stockapi.exception.ApiException;
import com.example.stockapi.model.stock.Stock;
import com.example.stockapi.model.stock.StockSearchResult;
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
    StockDataRepo stockDataRepo;

    @Autowired
    public StockController(StockDataRepo stockDataRepo) {
        this.stockDataRepo = stockDataRepo;
    }

    @GetMapping("/search/{stockName}")
    public ResponseEntity<List<StockSearchResult>> searchForStock(@PathVariable String stockName) throws ApiException {
        return new ResponseEntity<>(this.stockDataRepo.searchForSymbol(stockName), HttpStatus.OK);
    }

    @GetMapping("/stock/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) throws ApiException {
        return new ResponseEntity<>(this.stockDataRepo.getStockFromSymbol(symbol), HttpStatus.OK);
    }

}
