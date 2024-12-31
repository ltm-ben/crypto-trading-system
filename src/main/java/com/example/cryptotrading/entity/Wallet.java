package com.example.cryptotrading.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

import static com.example.cryptotrading.constant.WalletEntityConstant.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = TBL_WALLET)
public class Wallet {
    @Id
    @Column(name = USER_ID)
    private String userId;

    @Column(name = USDT_BALANCE)
    private Double usdtBalance;

    @Column(name = BTC_BALANCE)
    private Double btcBalance;

    @Column(name = ETH_BALANCE)
    private Double ethBalance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Wallet wallet = (Wallet) o;
        return userId != null && Objects.equals(userId, wallet.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}