package com.example.stockapi.model;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {

    @NonNull
    private String name;
    private String email;
    private String mobileNumber;

    @NonNull
    private String accountNumber;

    private Double netInvested;
    private Double netPortfolioValue;

    private List<Investment> investments;

    private Double unrealizedGains;
    private Double unrealizedGainsPercentage;

    private Double realizedGains;
    private Double realizedGainsPercentage;

    public User(@NonNull String name, String email, String mobileNumber, @NonNull String accountNumber, Double netInvested, Double netPortfolioValue, List<Investment> investments, Double unrealizedGains, Double unrealizedGainsPercentage, Double realizedGains, Double realizedGainsPercentage) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.accountNumber = accountNumber;
        this.netInvested = netInvested;
        this.netPortfolioValue = netPortfolioValue;
        this.investments = investments;
        this.unrealizedGains = unrealizedGains;
        this.unrealizedGainsPercentage = unrealizedGainsPercentage;
        this.realizedGains = realizedGains;
        this.realizedGainsPercentage = realizedGainsPercentage;
    }

    public User(@NonNull String name, String email, String mobileNumber, @NonNull String accountNumber) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.accountNumber = accountNumber;
        this.netInvested = 0.0;
        this.netPortfolioValue = 0.0;
        this.investments = new ArrayList<>();
        this.unrealizedGains = 0.0;
        this.unrealizedGainsPercentage = 0.0;
        this.realizedGains = 0.0;
        this.realizedGainsPercentage = 0.0;
    }

    public void invest(Investment investment) {
        this.investments.add(investment);
        this.netInvested += investment.getNetInvested();
        this.unrealizedGains += investment.getNetProfit();
    }

    public void buy(Stock stock, Double buyPrice) {


        Investment previousInvestment = null;

        for (Investment investment : investments)
            if (investment.getStock().equals(stock)) {
                previousInvestment = investment;
                break;
            }




    }


}
