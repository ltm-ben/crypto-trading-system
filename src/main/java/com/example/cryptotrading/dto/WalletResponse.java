package com.example.cryptotrading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private String userId;
    private Double usdtBalance;
    private Double ethBalance;
    private Double btcBalance;
}