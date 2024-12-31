package com.example.cryptotrading.client.huobi.dto;

import lombok.Data;

@Data
public class RetrieveHuobiTickerDataDto {
    private String symbol;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double amount;
    private Double vol;
    private Integer count;
    private Double bid;
    private Double bidSize;
    private Double ask;
    private Double askSize;
}