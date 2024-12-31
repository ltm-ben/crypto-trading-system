package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.CryptoPriceResponse;
import com.example.cryptotrading.dto.TradeRequest;
import com.example.cryptotrading.dto.TradeTransactionResponse;
import com.example.cryptotrading.dto.WalletResponse;
import com.example.cryptotrading.entity.TradeTransaction;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.repository.TradeTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.cryptotrading.constant.CryptoTradingSvcConstant.*;
import static com.example.cryptotrading.exception.CryptoTradingErrorType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeService {
    private final WalletService walletService;
    private final CryptoPriceService cryptoPriceService;
    private final TradeTransactionRepository tradeTransactionRepository;

    public List<TradeTransactionResponse> getUserTrades(String userId) {
        List<TradeTransaction> trades = tradeTransactionRepository.findByUserId(userId);
        return mapToResponse(trades);
    }

    @Transactional(readOnly = true)
    public void processTrade(TradeRequest tradeRequest) throws CryptoTradingException {
        WalletResponse wallet = walletService.getWalletResponse(tradeRequest.getUserId());

        CryptoPriceResponse marketPrice = cryptoPriceService.getBestPrice(tradeRequest.getPair());

        if (tradeRequest.getType().equalsIgnoreCase(TRADE_TYPE_BUY)) {
            performBuy(tradeRequest, wallet, marketPrice);
        } else if (tradeRequest.getType().equalsIgnoreCase(TRADE_TYPE_SELL)) {
            performSell(tradeRequest, wallet, marketPrice);
        } else {
            throw new CryptoTradingException(ERR_INVALID_TRADE_TYPE, "Invalid trade type: " + tradeRequest.getType());
        }

        walletService.updateWallet(wallet);

        TradeTransaction transaction = new TradeTransaction();
        BeanUtils.copyProperties(tradeRequest, transaction);
        transaction.setTimestamp(LocalDateTime.now());
        tradeTransactionRepository.saveAndFlush(transaction);
    }

    private void performBuy(TradeRequest tradeRequest, WalletResponse wallet, CryptoPriceResponse cryptoPrice)
            throws CryptoTradingException {
        if (tradeRequest.getPrice() >= cryptoPrice.getAskPrice() * tradeRequest.getAmount()) {
            double totalCost = tradeRequest.getPrice();
            if (wallet.getUsdtBalance() < totalCost) {
                throw new CryptoTradingException(ERR_INSUFFICIENT_FUNDS, "Insufficient USDT balance for the transaction.");
            }

            wallet.setUsdtBalance(wallet.getUsdtBalance() - totalCost);
            if (TRADING_PAIR_ETHUSDT.equalsIgnoreCase(tradeRequest.getPair())) {
                wallet.setEthBalance(wallet.getEthBalance() + tradeRequest.getAmount());
            } else if (TRADING_PAIR_BTCUSDT.equalsIgnoreCase(tradeRequest.getPair())) {
                wallet.setBtcBalance(wallet.getBtcBalance() + tradeRequest.getAmount());
            } else {
                throw new CryptoTradingException(ERR_INVALID_PAIR, "Unsupported trading pair: " + tradeRequest.getPair());
            }
        } else {
            throw new CryptoTradingException(ERR_PRICE_MISMATCH,
                    String.format("Provided price (%.2f) is lower than the current ask price (%.2f).",
                            tradeRequest.getPrice(), cryptoPrice.getAskPrice() * tradeRequest.getAmount()));
        }
    }

    private void performSell(TradeRequest tradeRequest, WalletResponse wallet, CryptoPriceResponse cryptoPrice)
            throws CryptoTradingException {
        if (tradeRequest.getPrice() <= cryptoPrice.getBidPrice() * tradeRequest.getAmount()) {
            if (TRADING_PAIR_ETHUSDT.equalsIgnoreCase(tradeRequest.getPair())) {
                if (wallet.getEthBalance() < tradeRequest.getAmount()) {
                    throw new CryptoTradingException(ERR_INSUFFICIENT_CURRENCY, "Insufficient ETH balance for the transaction.");
                }
                wallet.setEthBalance(wallet.getEthBalance() - tradeRequest.getAmount());
            } else if (TRADING_PAIR_BTCUSDT.equalsIgnoreCase(tradeRequest.getPair())) {
                if (wallet.getBtcBalance() < tradeRequest.getAmount()) {
                    throw new CryptoTradingException(ERR_INSUFFICIENT_CURRENCY, "Insufficient BTC balance for the transaction.");
                }
                wallet.setBtcBalance(wallet.getBtcBalance() - tradeRequest.getAmount());
            } else {
                throw new CryptoTradingException(ERR_INVALID_PAIR, "Unsupported trading pair: " + tradeRequest.getPair());
            }

            wallet.setUsdtBalance(wallet.getUsdtBalance() + tradeRequest.getAmount() * tradeRequest.getPrice());
        } else {
            throw new CryptoTradingException(ERR_PRICE_MISMATCH,
                    String.format("Provided price (%.2f) is higher than the current bid price (%.2f).",
                            tradeRequest.getPrice(), cryptoPrice.getBidPrice() * tradeRequest.getAmount()));
        }
    }

    private List<TradeTransactionResponse> mapToResponse(List<TradeTransaction> tradeTransactions) {
        List<TradeTransactionResponse> responseList = new ArrayList<>();

        for (TradeTransaction tradeTransaction : tradeTransactions) {
            TradeTransactionResponse response = new TradeTransactionResponse();
            BeanUtils.copyProperties(tradeTransaction, response);
            responseList.add(response);
        }

        return responseList;
    }
}