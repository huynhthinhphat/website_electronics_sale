package com.tip.b18.electronicsales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tip.b18.electronicsales.enums.Delivery;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    private UUID id;
    private String orderCode;
    private String fullName;
    private String address;
    private String phoneNumber;
    private Status status;
    private PaymentMethod paymentMethod;
    private Delivery delivery;
    private String note;
    private BigDecimal feeDelivery;
    private BigDecimal totalPrice;
    private Integer totalQuantity;
    private boolean isFromCart;
    private LocalDateTime fromEstimateDate;
    private LocalDateTime toEstimateDate;
    private List<OrderDetailDTO> items;
    private LocalDateTime createdAt;
}
