package com.example.cryptotrading.controller;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.response.CryptoTradingResponseEntity;
import com.example.cryptotrading.service.CryptoPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.CryptoPriceControllerConstants.BASE_URL;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.CryptoPriceControllerConstants.GET_PRICE;
import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_GENERIC;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class CryptoPriceController {
    private final CryptoPriceService cryptoPriceService;

    @GetMapping(GET_PRICE)
    public ResponseEntity<?> getPrice(@PathVariable String pair) {
        try {
            CryptoPriceResponse response = cryptoPriceService.getBestPrice(pair);

            return ResponseEntity.ok(response);
        } catch (CryptoTradingException e) {
            return CryptoTradingResponseEntity.<String>builder()
                    .httpStatus(e.getError().getHttpStatusCode())
                    .errorCode(e.getError().getCode())
                    .errorMessage(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error fetching price for pair {}: {}", pair, e.getMessage(), e);
            return CryptoTradingResponseEntity.<String>builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(ERR_GENERIC.getCode())
                    .errorMessage(ERR_GENERIC.getDesc())
                    .build();
        }
    }
}