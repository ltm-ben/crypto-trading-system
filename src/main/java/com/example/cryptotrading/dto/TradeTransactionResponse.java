package com.example.cryptotrading.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.DATE_TIME_FORMAT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TradeTransactionResponse {
    private String pair;
    private String type;
    private Double amount;
    private Double price;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime timestamp;
}