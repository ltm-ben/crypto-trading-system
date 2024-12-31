package com.example.cryptotrading.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CryptoTradingSvcConstant {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class CryptoPriceControllerConstants {
        public static final String BASE_URL = "/api/price";
        public static final String GET_PRICE = "/{pair}";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class TradeControllerConstants {
        public static final String BASE_URL = "/api/trade";
        public static final String GET_TRADES = "/{userId}";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class WalletControllerConstants {
        public static final String BASE_URL = "/api/wallet";
        public static final String GET_WALLET = "/{userId}";
    }

    public static final String TRADE_TYPE_BUY = "BUY";
    public static final String TRADE_TYPE_SELL = "SELL";
    public static final String TRADING_PAIR_ETHUSDT = "ETHUSDT";
    public static final String TRADING_PAIR_BTCUSDT = "BTCUSDT";
    public static final List<String> SUPPORTED_TRADING_PAIRS = List.of(TRADING_PAIR_ETHUSDT, TRADING_PAIR_BTCUSDT);
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}