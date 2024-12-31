package com.example.cryptotrading.repository;

import com.example.cryptotrading.entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, String> {
    Optional<CryptoPrice> findByPair(String pair);
}