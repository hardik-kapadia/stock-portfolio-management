package com.example.stockapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Investment {

    private final Stock stock;

    private Integer quantity;

    private Double netInvested;
    private Double currentValue;

    private Double netProfit;
    private Double netProfitPercentage;

    private Double averageBuyPrice;

    // Assuming that the stock is bought at Market Price
    public Investment(Stock stock, Integer quantity) {

        this.stock = stock;
        this.quantity = quantity;

        this.netInvested = stock.getLTP() * quantity;
        this.currentValue = this.netInvested;

        this.netProfit = 0.0;
        this.netProfitPercentage = 0.0;

        this.averageBuyPrice = stock.getLTP();

    }

    // Supplying just the Stock, the Quantity and the buy Price
    public Investment(Stock stock, Integer quantity, Double averageBuyPrice) {

        this.stock = stock;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;

        this.netInvested = averageBuyPrice * quantity;

        refresh();
    }

    public void refresh() {

        this.currentValue = this.stock.getLTP() * quantity;

        this.netProfit = netInvested - currentValue;
        this.netProfitPercentage = (netProfit / netInvested) * 100;
    }


}
