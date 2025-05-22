package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class Cart extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;
}
