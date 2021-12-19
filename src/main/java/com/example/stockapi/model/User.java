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

    private Double netProfit;
    private Double netProfitPercentage;

    public User(@NonNull String name, String email, String mobileNumber, @NonNull String accountNumber, Double netInvested, Double netPortfolioValue, List<Investment> investments, Double netProfit, Double netProfitPercentage) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.accountNumber = accountNumber;
        this.netInvested = netInvested;
        this.netPortfolioValue = netPortfolioValue;
        this.investments = investments;
        this.netProfit = netProfit;
        this.netProfitPercentage = netProfitPercentage;
    }

    public User(@NonNull String name, String email, String mobileNumber, @NonNull String accountNumber) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.accountNumber = accountNumber;
        this.netInvested = 0.0;
        this.netPortfolioValue = 0.0;
        this.investments = new ArrayList<>();
        this.netProfit = 0.0;
        this.netProfitPercentage = 0.0;
    }

    public void invest(Investment investment){
        this.investments.add(investment);
        this.netInvested += investment.getNetInvested();
        this.netProfit += investment.getNetProfit();
    }

}
