package com.tip.b18.electronicsales.enums;

import lombok.*;

@Getter
@AllArgsConstructor
public enum Delivery {
    VIETTEL_POST("Viettel Post"),
    FAST_DELIVERY("Fast Delivery"),
    JT_EXPRESS("J&T Express");

    private final String displayName;
}
