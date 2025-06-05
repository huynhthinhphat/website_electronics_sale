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
    WAITING_FOR_PAYMENT("đang đợi để thanh toán"),
    RETURNING("đang hoàn hàng"),
    REFUNDED("đã hoàn tiền");

    private final String displayName;
}
