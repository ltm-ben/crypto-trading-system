package com.example.cryptotrading.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
public class CryptoTradingResponseEntity<T> extends ResponseEntity<CryptoTradingResponse> {

    public CryptoTradingResponseEntity(CryptoTradingResponse body, HttpStatus httpStatus) {
        super(body, httpStatus);
    }

    public static class CryptoTradingResponseEntityBuilder<T> {
        private String errorCode;
        private String errorMessage;
        private HttpStatus httpStatus;

        public CryptoTradingResponseEntityBuilder<T> errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public CryptoTradingResponseEntityBuilder<T> errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public CryptoTradingResponseEntityBuilder<T> httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public CryptoTradingResponseEntity<T> build() {
            CryptoTradingResponse responseBody = new CryptoTradingResponse(errorCode, errorMessage, httpStatus);
            return new CryptoTradingResponseEntity<>(responseBody, httpStatus);
        }
    }
}