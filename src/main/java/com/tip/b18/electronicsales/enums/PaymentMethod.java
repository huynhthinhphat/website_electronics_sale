package com.tip.b18.electronicsales.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    COD("Cash on Delivery"),
    MOMO("MoMo E-Wallet"),
    ZALOPAY("ZaloPay E-Wallet");

    private final String displayName;
}
