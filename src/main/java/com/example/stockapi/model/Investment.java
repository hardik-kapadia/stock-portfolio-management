package com.example.stockapi.model;

import com.example.stockapi.model.stock.Stock;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Investment {

    @Id
    Integer id;

    @OneToOne
    private Stock stock;

    private Integer quantity;

    private Double netInvested;
    private Double currentValue;

    private Double netProfit;
    private Double netProfitPercentage;

    private Double averageBuyPrice;

    // Supplying just the Stock, the Quantity and the buy Price
    public Investment(Stock stock, Integer quantity, Double averageBuyPrice) {

        this.stock = stock;

        buy(quantity, averageBuyPrice);

        refresh();
    }

    // Updates stock data based on latest LTP.
    void refresh() {

        this.currentValue = this.stock.getLTP() * quantity;

        this.netProfit = netInvested - currentValue;
        this.netProfitPercentage = (netProfit / netInvested) * 100;

    }

    // Sells the stock and returns the income from the sale
    public Double sell(int quantity, Double sellingPrice) {

        this.quantity -= quantity;
        this.netInvested -= this.quantity * averageBuyPrice;

        refresh();

        return (quantity * sellingPrice);
    }

    // Selling the stock at Market price and returning the income from the sale
    public Double sell(int quantity) {
        return sell(quantity, this.stock.getLTP());
    }

    // Selling all the stock at Market Price. and returning the income from the sale
    public Double sell() {
        return sell(this.quantity, this.stock.getLTP());
    }

    // Buy a certain quantity of the stock at the specified price.
    public void buy(int quantity, Double buyPrice) {
        this.quantity += quantity;
        this.netInvested += quantity * buyPrice;
        this.averageBuyPrice = this.netInvested / this.quantity;

        refresh();

    }

    // Buy a certain quantity of the stock at the Market Price.
    public void buy(int quantity) {
        buy(quantity, this.stock.getLTP());
    }
}
