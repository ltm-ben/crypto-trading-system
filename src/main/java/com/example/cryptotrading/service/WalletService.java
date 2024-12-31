package com.example.cryptotrading.service;

import com.example.cryptotrading.dto.WalletResponse;
import com.example.cryptotrading.entity.Wallet;
import com.example.cryptotrading.exception.CryptoTradingException;
import com.example.cryptotrading.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.cryptotrading.exception.CryptoTradingErrorType.ERR_WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;

    @Transactional(readOnly = true)
    public WalletResponse getWalletResponse(String userId) throws CryptoTradingException {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CryptoTradingException(ERR_WALLET_NOT_FOUND, "Wallet not found for userId: " + userId));

        WalletResponse walletResponse = new WalletResponse();
        BeanUtils.copyProperties(wallet, walletResponse);
        return walletResponse;
    }

    @Transactional
    public WalletResponse updateWallet(WalletResponse walletResponse) throws CryptoTradingException {
        Wallet existingWallet = walletRepository.findByUserId(walletResponse.getUserId())
                .orElse(null);

        if (existingWallet == null) {
            throw new CryptoTradingException(ERR_WALLET_NOT_FOUND, "Wallet not found for userId: " + walletResponse.getUserId());
        }

        existingWallet.setUsdtBalance(walletResponse.getUsdtBalance());
        existingWallet.setEthBalance(walletResponse.getEthBalance());
        existingWallet.setBtcBalance(walletResponse.getBtcBalance());

        Wallet savedWallet = walletRepository.saveAndFlush(existingWallet);

        WalletResponse response = new WalletResponse();
        BeanUtils.copyProperties(savedWallet, response);

        return response;
    }
}