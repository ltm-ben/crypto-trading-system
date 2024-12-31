package com.example.cryptotrading.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CryptoTradingException extends BaseException {
    public CryptoTradingException(CryptoTradingErrorType errorType) {
        super();
        this.setError(errorType);
    }

    public CryptoTradingException(CryptoTradingErrorType errorType, String message) {
        super(message);
        this.setError(errorType);
    }

    public CryptoTradingException(CryptoTradingErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.setError(errorType);
    }

    public CryptoTradingException(CryptoTradingErrorType errorType, Throwable cause) {
        super(cause);
        this.setError(errorType);
    }
}