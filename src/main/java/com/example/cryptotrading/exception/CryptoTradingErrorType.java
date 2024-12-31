package com.example.cryptotrading.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CryptoTradingErrorType implements ErrorType {
    ERR_GENERIC("CORELIB-GEN-000", "Something went wrong.", HttpStatus.BAD_REQUEST),
    ERR_INSUFFICIENT_FUNDS("TRADING-ERR-001", "Insufficient funds to perform the trade.", HttpStatus.BAD_REQUEST),
    ERR_INVALID_PAIR("TRADING-ERR-002", "The specified trading pair is invalid.", HttpStatus.BAD_REQUEST),
    ERR_PRICE_MISMATCH("TRADING-ERR-003", "The specified price does not match the current market price.", HttpStatus.BAD_REQUEST),
    ERR_USER_NOT_FOUND("TRADING-ERR-004", "User not found.", HttpStatus.NOT_FOUND),
    ERR_TRADE_NOT_FOUND("TRADING-ERR-005", "Trade transaction not found.", HttpStatus.NOT_FOUND),
    ERR_WALLET_NOT_FOUND("TRADING-ERR-006", "Wallet not found.", HttpStatus.NOT_FOUND),
    ERR_PRICE_NOT_FOUND("TRADING-ERR-007", "Price not found for pair.", HttpStatus.NOT_FOUND),
    ERR_INVALID_TRADE_TYPE("TRADING-ERR-008", "Invalid trade type.", HttpStatus.BAD_REQUEST),
    ERR_INSUFFICIENT_CURRENCY("TRADING-ERR-009", "Insufficient cryptocurrency to perform the trade.", HttpStatus.BAD_REQUEST),
    ERR_INTERNAL_SERVER_ERROR("TRADING-ERR-010", "Unexpected error while calling client API.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String desc;
    private final HttpStatus httpStatusCode;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }
}