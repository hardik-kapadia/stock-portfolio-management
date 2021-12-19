package com.example.stockapi.model;

import lombok.Data;

@Data
public class Stock {

    private String name;
    private Exchange exchange;

    private Double PE;
    private Double EPS;

    private Double LTP;

    private Double previousOpen;
    private Double previousClose;

    private Double latestHigh;
    private Double latestLow;

    private Double fiftyTwoWeekHigh;
    private Double fiftyTwoWeekLow;

}
