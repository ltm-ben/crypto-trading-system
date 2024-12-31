package com.example.cryptotrading.controller;

import com.example.cryptotrading.dto.TradeRequest;
import com.example.cryptotrading.dto.TradeTransactionResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.response.CryptoTradingResponseEntity;
import com.example.cryptotrading.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.TradeControllerConstants.BASE_URL;
import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.TradeControllerConstants.GET_TRADES;
import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_GENERIC;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@Slf4j
public class TradeController {
    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<?> trade(@RequestBody @Valid TradeRequest request) {
        try {
            tradeService.processTrade(request);

            return ResponseEntity.ok("Trade processed successfully");
        } catch (CryptoTradingException e) {
            return CryptoTradingResponseEntity.<String>builder()
                    .httpStatus(e.getError().getHttpStatusCode())
                    .errorCode(e.getError().getCode())
                    .errorMessage(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error processing trade: {}", e.getMessage(), e);
            return CryptoTradingResponseEntity.<String>builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(ERR_GENERIC.getCode())
                    .errorMessage(ERR_GENERIC.getDesc())
                    .build();
        }
    }

    @GetMapping(GET_TRADES)
    public ResponseEntity<?> getTrades(@PathVariable String userId) {
        try {
            List<TradeTransactionResponse> responses = tradeService.getUserTrades(userId);

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error fetching trades for user {}: {}", userId, e.getMessage(), e);
            return CryptoTradingResponseEntity.<String>builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(ERR_GENERIC.getCode())
                    .errorMessage(ERR_GENERIC.getDesc())
                    .build();
        }
    }
}