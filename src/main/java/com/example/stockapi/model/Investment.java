package com.example.stockapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Investment {

    int id;

    private final Stock stock;

    private Integer quantity;

    private Double netInvested;
    private Double currentValue;

    private Double netProfit;
    private Double netProfitPercentage;

    private Double averageBuyPrice;

    // Assuming that the stock is bought at Market Price
    public Investment(Stock stock, Integer quantity) {
        this(stock, quantity, stock.getLTP());
    }

    // Supplying just the Stock, the Quantity and the buy Price
    public Investment(Stock stock, Integer quantity, Double averageBuyPrice) {

        this.stock = stock;

        buy(quantity, averageBuyPrice);

        refresh();
    }

    // Updates stock data
    public void refresh() {

        this.currentValue = this.stock.getLTP() * quantity;

        this.netProfit = netInvested - currentValue;
        this.netProfitPercentage = (netProfit / netInvested) * 100;

    }

    public Double sell(int quantity, Double sellingPrice) {

        this.quantity -= quantity;
        this.netInvested -= this.quantity * averageBuyPrice;

        refresh();

        return quantity * sellingPrice;
    }

    public Double sell(int quantity) {
        return sell(quantity, this.stock.getLTP());
    }

    public Double sell() {
        return sell(this.quantity, this.stock.getLTP());
    }

    public void buy(int quantity, Double buyPrice) {
        this.quantity += quantity;
        this.netInvested += quantity * buyPrice;
        this.averageBuyPrice = this.netInvested / this.quantity;

        refresh();

    }

    public void buy(int quantity) {
        buy(quantity, this.stock.getLTP());
    }


}
