package com.example.cryptotrading.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TradeRequest {

    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotNull(message = "Pair cannot be null")
    @Pattern(regexp = "ETHUSDT|BTCUSDT", message = "Pair must be either ETHUSDT or BTCUSDT")
    private String pair;

    @NotNull(message = "Type cannot be null")
    @Pattern(regexp = "BUY|SELL", message = "Type must be either BUY or SELL")
    private String type;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private Double price;
}