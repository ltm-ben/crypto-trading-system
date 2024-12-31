package com.example.cryptotrading.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.cryptotrading.constant.TradeTransactionEntityConstant.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = TBL_TRADE_TRANSACTION)
public class TradeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @Column(name = USER_ID)
    private String userId;

    @Column(name = PAIR)
    private String pair;

    @Column(name = TYPE)
    private String type;

    @Column(name = AMOUNT)
    private Double amount;

    @Column(name = PRICE)
    private Double price;

    @Column(name = TIMESTAMP)
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TradeTransaction that = (TradeTransaction) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}