package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.entity.CryptoPrice;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_PRICE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoPriceService {
    private final CryptoPriceRepository cryptoPriceRepository;
    private final CacheService cacheService;

    /**
     * Aggregates the prices from multiple sources (Binance, Huobi) and returns the best prices.
     */
    public List<CryptoPriceResponse> aggregatePrices(List<CryptoPriceResponse> binancePrices, List<CryptoPriceResponse> huobiPrices) {
        List<CryptoPriceResponse> combinedPrices = Stream.concat(binancePrices.stream(), huobiPrices.stream()).toList();

        return combinedPrices.stream()
                .collect(Collectors.groupingBy(CryptoPriceResponse::getPair))
                .entrySet()
                .stream()
                .map(entry -> {
                    String pair = entry.getKey();
                    List<CryptoPriceResponse> prices = entry.getValue();

                    Double bestBid = prices.stream().mapToDouble(CryptoPriceResponse::getBidPrice).max().orElse(0.0);
                    Double bestAsk = prices.stream().mapToDouble(CryptoPriceResponse::getAskPrice).min().orElse(0.0);

                    CryptoPriceResponse aggregatedPrice = new CryptoPriceResponse();
                    aggregatedPrice.setPair(pair);
                    aggregatedPrice.setBidPrice(bestBid);
                    aggregatedPrice.setAskPrice(bestAsk);

                    return aggregatedPrice;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CryptoPriceResponse getBestPrice(String pair) throws CryptoTradingException {
        Optional<CryptoPriceResponse> cachedPrice = cacheService.getFromCache(pair);
        if (cachedPrice.isPresent()) {
            return cachedPrice.get();
        }

        CryptoPrice cryptoPrice = cryptoPriceRepository.findByPair(pair)
                .orElseThrow(() -> new CryptoTradingException(ERR_PRICE_NOT_FOUND, "Price not found for pair: " + pair));

        CryptoPriceResponse response = mapToResponse(cryptoPrice);
        cacheService.updateCache(pair, response);
        return response;
    }

    private CryptoPriceResponse mapToResponse(CryptoPrice cryptoPrice) {
        CryptoPriceResponse response = new CryptoPriceResponse();
        BeanUtils.copyProperties(cryptoPrice, response);
        return response;
    }

    @Transactional
    public void savePrices(List<CryptoPriceResponse> prices) {
        for (CryptoPriceResponse price : prices) {
            updateOrCreateCryptoPrice(price);
        }

        prices.forEach(price -> cacheService.updateCache(price.getPair(), price));
    }

    /**
     * Update the price if exists or create a new one.
     */
    @Transactional
    public void updateOrCreateCryptoPrice(CryptoPriceResponse price) {
        Optional<CryptoPrice> existingPriceOpt = cryptoPriceRepository.findByPair(price.getPair());

        if (existingPriceOpt.isPresent()) {
            CryptoPrice existingPrice = existingPriceOpt.get();
            existingPrice.setBidPrice(price.getBidPrice());
            existingPrice.setAskPrice(price.getAskPrice());
            cryptoPriceRepository.saveAndFlush(existingPrice);
        } else {
            CryptoPrice cryptoPrice = new CryptoPrice();
            BeanUtils.copyProperties(price, cryptoPrice);
            cryptoPriceRepository.saveAndFlush(cryptoPrice);
        }
    }
}