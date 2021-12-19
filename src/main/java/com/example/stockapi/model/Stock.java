package com.example.stockapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class Stock {

    private final String id;

    private final String name;
    private final String symbol;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final Exchange exchange;

    private Double PE;
    private Double EPS;

    private Double LTP;

    private Double previousOpen;
    private Double previousClose;

    private Double latestHigh;
    private Double latestLow;

    private Double fiftyTwoWeekHigh;
    private Double fiftyTwoWeekLow;


    public Stock(String name, String symbol, String exchange, Double PE, Double EPS, Double LTP, Double previousOpen, Double previousClose, Double latestHigh, Double latestLow, Double fiftyTwoWeekHigh, Double fiftyTwoWeekLow) {
        this.name = name;
        this.symbol = symbol;
        this.exchange = Exchange.valueOf(exchange);
        this.PE = PE;
        this.EPS = EPS;
        this.LTP = LTP;
        this.previousOpen = previousOpen;
        this.previousClose = previousClose;
        this.latestHigh = latestHigh;
        this.latestLow = latestLow;
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
        this.id = this.exchange.toString() + "-" + this.symbol;
    }

    public Stock(String name, String symbol, String exchange) {
        this.name = name;
        this.symbol = symbol;
        this.exchange = Exchange.valueOf(exchange);
        this.id = this.exchange.toString() + "-" + this.symbol;
    }

}
