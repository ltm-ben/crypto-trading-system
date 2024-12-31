package com.example.cryptotrading.client.binance.dto;

import lombok.Data;

@Data
public class RetrieveBinanceTickerDto {
    private String symbol;
    private String bidPrice;
    private String bidQty;
    private String askPrice;
    private String askQty;
}
