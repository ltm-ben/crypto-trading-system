package com.example.cryptotrading.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeTransactionEntityConstant {
    public static final String TBL_TRADE_TRANSACTION = "tbl_trade_transaction";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String PAIR = "pair";
    public static final String TYPE = "type";
    public static final String AMOUNT = "amount";
    public static final String PRICE = "price";
    public static final String TIMESTAMP = "timestamp";
}