package com.example.cryptotrading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoPriceResponse {
    private String pair;
    private Double bidPrice;
    private Double askPrice;
}