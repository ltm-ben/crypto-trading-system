package com.example.cryptotrading.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

import static com.example.cryptotrading.constant.CryptoPriceEntityConstant.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = TBL_CRYPTO_PRICE)
public class CryptoPrice {
    @Id
    @Column(name = PAIR)
    private String pair;

    @Column(name = BID_PRICE)
    private Double bidPrice;

    @Column(name = ASK_PRICE)
    private Double askPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CryptoPrice that = (CryptoPrice) o;
        return pair != null && Objects.equals(pair, that.pair);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}