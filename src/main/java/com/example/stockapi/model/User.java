package com.example.stockapi.model;

import com.example.stockapi.model.role.Role;
import com.example.stockapi.model.stock.Stock;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.PERSIST;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @NonNull
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobileNumber;

    private String password;

    @Column(unique = true)
    private String accountNumber;

    private Double netInvested;
    private Double netPortfolioValue;

    //    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL, CascadeType.REMOVE})
    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, DETACH})
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
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

    @JsonCreator
    public User(@NonNull String name, String email, String mobileNumber, @NonNull String password, @NonNull String accountNumber) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
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
        this.netPortfolioValue += investment.getNetInvested();
        this.investments.add(investment);
        refresh();
    }

    public void refresh() {

        AtomicReference<Double> tempUnrealizedGains = new AtomicReference<>(0.0);
        AtomicReference<Double> tempPortfolioValue = new AtomicReference<>(0.0);

        investments.forEach(i ->
        {
            tempUnrealizedGains.updateAndGet(v -> v + i.getNetProfit());
            tempPortfolioValue.updateAndGet(v -> v + i.getCurrentValue());
        });

        this.setUnrealizedGains(tempUnrealizedGains.get());
        this.setNetPortfolioValue(tempPortfolioValue.get());

    }

    public void setUnrealizedGains(Double unrealizedGains) {
        this.unrealizedGains = unrealizedGains;
        this.setUnrealizedGainsPercentage(unrealizedGains * 100 / this.netInvested);
    }

    public void buy(Stock stock, Integer quantity, Double buyPrice) {

        System.out.println(this + " buying " + quantity + " of " + stock + " at " + buyPrice);

        Investment previousInvestment = null;

        for (Investment investment : investments)
            if (investment.getStock().equals(stock)) {
                previousInvestment = investment;
                break;
            }

        if (previousInvestment != null)
            previousInvestment.buy(quantity, buyPrice);
        else
            this.invest(new Investment(stock, quantity, buyPrice, this));

        refresh();

        System.out.println("After buying: investments: " + this.investments);

    }

    public void sell(Stock stock, Integer quantity, Double sellingPrice) throws IllegalArgumentException {

        Investment ownedInvestment = null;

        for (Investment investment : investments)
            if (investment.getStock().equals(stock)) {
                ownedInvestment = investment;
                break;
            }

        if (ownedInvestment == null)
            throw new IllegalArgumentException("You don't own this stock");

        if (quantity > ownedInvestment.getQuantity())
            throw new IllegalArgumentException("You don't own enough quantity of this stock");

        sell(ownedInvestment, quantity, sellingPrice);

    }

    public void sell(int investmentId, Integer quantity, Double sellingPrice) throws IllegalArgumentException {

        Investment ownedInvestment = null;

        for (Investment investment : investments)
            if (investment.getId().equals(investmentId)) {
                ownedInvestment = investment;
                break;
            }

        if (ownedInvestment == null)
            throw new IllegalArgumentException("You don't own this stock");

        if (quantity > ownedInvestment.getQuantity())
            throw new IllegalArgumentException("You don't own enough quantity of this stock");

        sell(ownedInvestment, quantity, sellingPrice);

    }

    public void sell(Investment investment, Integer quantity, Double sellingPrice) {

        this.netInvested -= investment.getNetInvested();

        this.realizedGains += investment.sell(quantity, sellingPrice) - investment.getNetInvested();

        if (investment.getQuantity() == 0)
            this.investments.remove(investment);

        refresh();

    }

    public Optional<Investment> getInvestmentFromStock(String stockSymbol) {

        for (Investment i : this.getInvestments()) {
            if (i.getStock().getSymbol().equals(stockSymbol))
                return Optional.of(i);
        }

        return Optional.empty();

    }

    public Optional<Investment> getInvestmentFromId(int investmentId) {

        for (Investment i : this.getInvestments()) {
            if (i.getId() == investmentId)
                return Optional.of(i);
        }

        return Optional.empty();

    }
}
