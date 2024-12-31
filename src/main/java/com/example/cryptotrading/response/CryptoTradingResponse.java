package com.example.cryptotrading.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CryptoTradingResponse {
    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;
}