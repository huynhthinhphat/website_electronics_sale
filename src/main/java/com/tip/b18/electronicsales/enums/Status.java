package com.tip.b18.electronicsales.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum Status {
    PENDING("đang chờ xử lý"),
    SHIPPING("đang vận chuyển"),
    COMPLETED("đã hoàn thành"),
    CANCELED("đã hủy"),
    PAID_BUT_OUT_OF_STOCK("đã thanh toán nhưng hết hàng"),
    WAITING_FOR_PAYMENT("đang đợi để thanh toán");

    private final String displayName;
}
