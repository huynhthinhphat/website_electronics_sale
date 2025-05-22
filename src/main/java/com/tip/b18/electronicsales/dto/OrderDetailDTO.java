package com.tip.b18.electronicsales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailDTO {
    private UUID id;
    private UUID orderId;
    private String sku;
    private String name;
    private int quantity;
    private BigDecimal price;
    private BigDecimal priceAtTime;
    private BigDecimal totalPrice;
    private String mainImageUrl;
    private String color;
}
