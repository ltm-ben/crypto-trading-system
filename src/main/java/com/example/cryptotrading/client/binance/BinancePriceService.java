package com.example.cryptotrading.client.binance;

import com.example.cryptotrading.client.binance.dto.RetrieveBinanceTickerDto;
import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.properties.BinanceEndpointProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_GENERIC;
import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class BinancePriceService {
    private final RestTemplate restTemplate;
    private final BinanceEndpointProperties endpointProperties;

    public List<CryptoPriceResponse> getBestPrices(List<String> tradingPairs) throws CryptoTradingException {
        List<CryptoPriceResponse> prices = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(endpointProperties.getBestPrices().toUrl(), String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            List<RetrieveBinanceTickerDto> tickerDtos = objectMapper.readValue(response, new TypeReference<>() {
            });

            tickerDtos.stream()
                    .filter(dto -> tradingPairs.contains(dto.getSymbol()))
                    .forEach(dto -> {
                        CryptoPriceResponse cryptoPrice = new CryptoPriceResponse();
                        cryptoPrice.setPair(dto.getSymbol());
                        cryptoPrice.setBidPrice(Double.valueOf(dto.getBidPrice()));
                        cryptoPrice.setAskPrice(Double.valueOf(dto.getAskPrice()));
                        prices.add(cryptoPrice);
                    });
        } catch (HttpClientErrorException e) {
            throw new CryptoTradingException(ERR_GENERIC, "Error fetching prices from Binance: " + e.getMessage());
        } catch (Exception e) {
            throw new CryptoTradingException(ERR_INTERNAL_SERVER_ERROR, "Unexpected error while processing Binance data: " + e.getMessage());
        }

        return prices;
    }
}