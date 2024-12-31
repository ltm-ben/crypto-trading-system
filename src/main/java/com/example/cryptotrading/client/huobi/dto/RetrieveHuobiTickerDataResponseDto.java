package com.example.cryptotrading.client.huobi.dto;

import lombok.Data;

import java.util.List;

@Data
public class RetrieveHuobiTickerDataResponseDto {
    private String status;
    private Long ts;
    private List<RetrieveHuobiTickerDataDto> data;
}