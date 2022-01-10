package com.example.stockapi.model.stock;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockSearchResult {

    @JsonAlias("2. name")
    private String name;

    @JsonAlias("4. region")
    private String region;

    @JsonAlias("1. symbol")
    private String symbol;

    @JsonAlias("9. matchScore")
    private Double matchScore;

}
