package com.example.cryptotrading.client.huobi;

import com.example.cryptotrading.client.huobi.dto.RetrieveHuobiTickerDataResponseDto;
import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.properties.HuobiEndpointProperties;
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
public class HuobiPriceService {
    private final RestTemplate restTemplate;
    private final HuobiEndpointProperties endpointProperties;

    public List<CryptoPriceResponse> getBestPrices(List<String> tradingPairs) throws CryptoTradingException {
        List<CryptoPriceResponse> prices = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(endpointProperties.getBestPrices().toUrl(), String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            RetrieveHuobiTickerDataResponseDto responseDto = objectMapper.readValue(response, RetrieveHuobiTickerDataResponseDto.class);

            List<String> lowerCaseTradingPairs = tradingPairs.stream()
                    .map(String::toLowerCase).toList();

            responseDto.getData().stream()
                    .filter(dto -> lowerCaseTradingPairs.contains(dto.getSymbol()))
                    .forEach(dto -> {
                        CryptoPriceResponse cryptoPrice = new CryptoPriceResponse();
                        cryptoPrice.setPair(dto.getSymbol().toUpperCase());
                        cryptoPrice.setBidPrice(dto.getBid());
                        cryptoPrice.setAskPrice(dto.getAsk());
                        prices.add(cryptoPrice);
                    });
        } catch (HttpClientErrorException e) {
            throw new CryptoTradingException(ERR_GENERIC, "Error fetching prices from Huobi: " + e.getMessage());
        } catch (Exception e) {
            throw new CryptoTradingException(ERR_INTERNAL_SERVER_ERROR, "Unexpected error while processing Huobi data: " + e.getMessage());
        }

        return prices;
    }
}