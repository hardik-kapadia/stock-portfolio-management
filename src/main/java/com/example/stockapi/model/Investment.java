package com.example.stockapi.model;

import lombok.Data;

@Data
public class Investment {

    private Stock stock;

    private Double netInvested;
    private Double currentValue;

    private Double netProfit;
    private Double netProfitPercentage;

    private Integer quantity;

    private Integer averageBuyPrice;
}
