package com.example.cryptotrading.scheduler;

import com.example.cryptotrading.client.binance.BinancePriceService;
import com.example.cryptotrading.client.huobi.HuobiPriceService;
import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.service.CryptoPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.SUPPORTED_TRADING_PAIRS;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceAggregator {
    private final BinancePriceService binancePriceService;
    private final HuobiPriceService huobiPriceService;
    private final CryptoPriceService cryptoPriceService;

    @Scheduled(fixedRate = 10000)
    public void updateBestPrices() throws CryptoTradingException {

        List<CryptoPriceResponse> binancePrices = binancePriceService.getBestPrices(SUPPORTED_TRADING_PAIRS);
        List<CryptoPriceResponse> huobiPrices = huobiPriceService.getBestPrices(SUPPORTED_TRADING_PAIRS);

        List<CryptoPriceResponse> bestPrices = cryptoPriceService.aggregatePrices(binancePrices, huobiPrices);

        cryptoPriceService.savePrices(bestPrices);
    }
}