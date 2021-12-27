package com.example.stockapi.model.ETF;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Cryptocurrency implements ETF{

    private final String name;
    private final String symbol;
    private double LTP;

}
