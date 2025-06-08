package com.tip.b18.electronicsales.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    MOMO("Thanh toán trực tuyến"),
    ZALOPAY("ZaloPay E-Wallet");

    private final String displayName;
}
