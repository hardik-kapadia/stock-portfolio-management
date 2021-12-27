package com.example.stockapi.model.ETF;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Cryptocurrency implements ETF{

    private final String name;
    private final String symbol;
    private Double LTP;

    @Override
    public Double getLTP() {
        return this.LTP;
    }

}
