package com.example.stockapi.model.stock;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Entity
public class Stock{

    @Id
    @JsonAlias("01. symbol")
    private String symbol;

    private String name;

    @JsonAlias("05. price")
    private Double LTP;

    @JsonAlias("02. open")
    private Double previousOpen;

    @JsonAlias("08. previous close")
    private Double previousClose;

    @JsonAlias("03. high")
    private Double high;

    @JsonAlias("04. low")
    private Double low;

    public Stock(String symbol) {
        this(symbol, symbol.split("\\.")[0]);
    }

    public Stock(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock stock)) return false;
        return this.getSymbol().equals(stock.getSymbol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSymbol());
    }

}
