package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Data
public class OrderDetail extends BaseIdEntity {
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "color", nullable = false, columnDefinition = "CHAR(25)")
    private String color;

    private int quantity;

    @Column(name = "price_at_time", length = 15, scale = 2, nullable = false)
    private BigDecimal priceAtTime;

    @Column(name = "total_price", length = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;
}
